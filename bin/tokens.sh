#!/bin/bash

JQ="$(which jq 2>/dev/null)"

[ -z "${JQ}" ] && {
    echo "error: no 'jq' found in PATH. run 'brew install jq'." >&2
    exit 1
}

PUBLIC_KEY=/tmp/pubkey.$$
PRIVATE_KEY=/tmp/privatekey.$$

cleanup() {
    rm -f ${PUBLIC_KEY}
    rm -f ${PRIVATE_KEY}
}

trap cleanup 'EXIT'

rsa_private_key=$(openssl genrsa -out ${PRIVATE_KEY} 1024 2>/dev/null)
rsa_public_key=$(openssl rsa -in ${PRIVATE_KEY} -pubout 2>/dev/null >${PUBLIC_KEY})

header='{
	"typ": "JWT",
	"alg": "RS256"
}'

payload='{
	"kid": "0001",
	"iss": "zod JWT generator",
	"scopes": ["convert", "read", "write"],
    "given_name": "Leon",
    "family_name": "Breedt",
    "email": "leon@test.com"
}'

# ensure token expires only one hour from now
payload=$(
	echo "${payload}" | jq --arg time_str "$(date +%s)" \
	'
	($time_str | tonumber) as $time_num
	| .iat=$time_num
	| .exp=($time_num + 900)
	'
)

base64_encode() {
	declare input=${1:-$(</dev/stdin)}
	printf '%s' "${input}" | openssl enc -base64 -A | tr '+/' '-_' | tr -d '='
}

json() {
	declare input=${1:-$(</dev/stdin)}
	printf '%s' "${input}" | jq -c .
}

rsa_sign() {
	declare input=${1:-$(</dev/stdin)}
	printf '%s' "${input}" | openssl dgst -binary -sha256 -sign ${PRIVATE_KEY} 
}

header_base64=$(echo "${header}" | json | base64_encode)
payload_base64=$(echo "${payload}" | json | base64_encode)

header_payload=$(echo "${header_base64}.${payload_base64}")
signature=$(echo "${header_payload}" | rsa_sign | base64_encode)

echo "PRIVATE  KEY:"
cat ${PRIVATE_KEY}
echo ""
echo "PUBLIC KEY:"
cat ${PUBLIC_KEY}
echo ""
echo "TOKEN:"
echo "${header_payload}.${signature}"
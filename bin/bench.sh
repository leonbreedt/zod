#!/bin/bash

WRK="$(which wrk 2>/dev/null)"
URL="http://localhost:8080"
PATH="${1:-/hello}"
CONCURRENCY="${2:-90}"

# generated using tokens.sh
JWT='eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJraWQiOiIwMDAxIiwiaXNzIjoiem9kIEpXVCBnZW5lcmF0b3IiLCJzY29wZXMiOlsiY29udmVydCIsInJlYWQiLCJ3cml0ZSJdLCJnaXZlbl9uYW1lIjoiTGVvbiIsImZhbWlseV9uYW1lIjoiQnJlZWR0IiwiZW1haWwiOiJsZW9uQHRlc3QuY29tIiwiaWF0IjoxNTE3MTAyMDczLCJleHAiOjE1MTcxMDU2NzN9.d0Tuot4dHascb8eVzX2g1iNQl1UxQalVEtnALTHt1IhKecCzt-xAqFhxR-f9lrTvdBXc7whQOMnEP308RPe3yeLVwuT2dWs8HObZoCgdoSv7udb2ayDDGjKELEP9pmM7B_7v0AS4iGSgss3q66SKGq61XYuXFDVIcxarfMKdbD0'

 [ -z "${WRK}" ] && {
    echo "error: no 'wrk' found in PATH. run 'brew install wrk'." >&2
    exit 1
}

${WRK} -c ${CONCURRENCY} -t ${CONCURRENCY} -H "Authorization: Bearer ${JWT}" ${URL}${PATH}
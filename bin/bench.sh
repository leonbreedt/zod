#!/bin/bash

WRK="$(which wrk 2>/dev/null)"
URL="http://localhost:8080"
PATH="${1:-/hello}"
CONCURRENCY="${2:-90}"

 [ -z "${WRK}" ] && {
    echo "error: no 'wrk' found in PATH. run 'brew install wrk'." >&2
    exit 1
}

${WRK} -c ${CONCURRENCY} -t ${CONCURRENCY} ${URL}${PATH}
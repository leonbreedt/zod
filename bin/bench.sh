#!/bin/bash

WRK="$(which wrk 2>/dev/null)"
URL="http://localhost:8080"
PATH="${1:-/hello}"
CONCURRENCY="${2:-90}"

# generated using http://kjur.github.io/jsjws/tool_jwt.html
JWT='eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0Iiwic3ViIjoibWFpbHRvOnJvb3RAbG9jYWxob3N0IiwibmJmIjoxNTE3MDQxMDkxLCJleHAiOjE1MTcwNDQ2OTEsImlhdCI6MTUxNzA0MTA5MSwianRpIjoiMjM5MjEzOTAiLCJ0eXAiOiJodHRwczovL3NlY3RvcjQyLmlvL3JlZ2lzdGVyIn0.EvBKft2PdpOTFV4n_MgpsP1H6zhrP9xdzkt-d9oaUGViYhQ3nUVPf705ZE83kEYe_Av9WcPehU3QiYEXtjd7UjvUlCeL4KKDhlyjbBJ2uvD5p6bTW-qDFC8yDN_FusZh38DHdFrnDWFAHCAZSSEgWmc-n28fQivDD_smlmMSimYx2lDRTgfs2rwUd7ViwewnwCg9CBJlS-ufsbBVJ5TQuh8UplI2PFH71JuFIiRixpTK5rn6Uu-nEBnyNyz2UaCn1s0QbfxG9wsrRQCrGDTqUfSuXFBTgXxYRBQCHRr-G4EGYLFUxaL6vz8Tu3Y0niDQC8WmRYzQ1INQSz55FSwwmA'

 [ -z "${WRK}" ] && {
    echo "error: no 'wrk' found in PATH. run 'brew install wrk'." >&2
    exit 1
}

${WRK} -c ${CONCURRENCY} -t ${CONCURRENCY} -H "Authorization: Bearer ${JWT}" ${URL}${PATH}
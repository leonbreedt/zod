#!/bin/bash

WRK="$(which wrk 2>/dev/null)"
URL="http://localhost:8080"
PATH="${1:-/hello}"
CONCURRENCY="${2:-90}"

# generated using http://kjur.github.io/jsjws/tool_jwt.html
JWT='eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0Iiwic3ViIjoibWFpbHRvOnJvb3RAbG9jYWxob3N0IiwibmJmIjoxNTE3MDM1ODM4LCJleHAiOjE1MTcwMzk0MzgsImlhdCI6MTUxNzAzNTgzOCwianRpIjoiMjM5MjEzOTAiLCJ0eXAiOiJodHRwczovL3NlY3RvcjQyLmlvL3JlZ2lzdGVyIn0.G_1zgOglbwbjSpIRFj6hUgenJN2cJo8EmldXT90EICWQgzVzItrrZIZDu1OIhdnnxcIejqceoYuLgUqNLMYbMdffvJ97GSSWwqEPlTH-Tcqeoj__564JWhlV_YbpmMeR4Ru7SXPJZMRV_xVuyu4khGNTyp0RJCbg2mDJ8rveGwTRp4AxvJdOpyGwSCEZPqPwiqpsDbZlpXrfKT_dyuww3HJB95lTWLeCTSLjjxWtYB3Z4sMjT8VfeMr8OIZ-xBlIBXI6p8QoeYl8jd82wboOKWLe0VP3NvHXeueel_xLVxxnqi_LZurboZ0Sg_UOdEV-gFwBCE9g6NCk2J5MLKc0Mw'

 [ -z "${WRK}" ] && {
    echo "error: no 'wrk' found in PATH. run 'brew install wrk'." >&2
    exit 1
}

${WRK} -c ${CONCURRENCY} -t ${CONCURRENCY} -H "Authorization: Bearer ${JWT}" ${URL}${PATH}
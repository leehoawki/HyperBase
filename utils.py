import requests
import sys
import urllib2, base64
import SargParse

s = SargParse.SargParser()
s.add_argument('host', message='Host')
s.add_argument('port', message='Port')
s.add_argument('table', message='Table Name')
s.add_argument('action', message='Action')
namespace = s.parse_arg()

host = namespace.host
port = namespace.port
table = namespace.table
action= namespace.action

if action == "create":
    url='http://' + host + ':' + port + '/HyperBase/api/' + table
    headers = {'content-type': 'application/json'}
    r = requests.post(url, data="create", headers=headers)
    print r.text
elif action == "delete":
    url = 'http://' + host + ':' + port + '/HyperBase/api/' + table
    headers = {'content-type': 'application/json'}
    r = requests.post(url, data="delete", headers=headers)
    print r.text
elif action.startswith("get:"):
    key = action[4:]
    url = 'http://' + host + ':' + port + '/HyperBase/api/' + table + '/' + key
    r = requests.get(url)
    print r.text

elif action.startswith("set:"):
    words = action.split(':')
    key = words[1]
    val = words[2]
    url = 'http://' + host + ':' + port + '/HyperBase/api/' + table + '/' + key
    headers = {'content-type': 'application/json'}
    r = requests.post(url, data=val, headers=headers)
    print r.text

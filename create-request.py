#!/usr/bin/python
import json
import base64

def createRequest(fname,outname):
    data = dict(filename=fname,
                __nt_name="B64FileData",
                b64_data="")
    out = base64.encodestring(open(data['filename'],'rb').read())
    data['b64_data'] = out
    open(outname,'wb').write(json.dumps(data))
    

createRequest('resources/corrupted.pdf',
              'resources/validate-request-with-corrupted.json')

createRequest('resources/test-pdfa-1b.pdf',
              'resources/validate-request-with-test-pdfa-1b.json')

createRequest('resources/test-pdf.pdf',
              'resources/validate-request-with-test-pdf.json')

    
# stream = open('resources/request-payload.bin','rb').read()
# data = json.loads(stream)
# open('/tmp/result.pdf','wb').write(base64.decodestring(data['b64_data']))

# import pdb; pdb.set_trace()
# stream = open('resources/validate-request-with-test-pdf.json','rb').read()
# data = json.loads(stream)
# open('/tmp/result.pdf','wb').write(base64.decodestring(data['b64_data']))

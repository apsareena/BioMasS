# BioMasS

Residue transition pattern (RTP) is a special technique that describes protein folding patterns, making it easier and more reliable to identify protein similarities than a typical sequence or structure-based approach. Layers algorithm is used for shape comparison and protein recognition. However, the existing algorithm processes each protein sequentially, resulting in more computational time to give the output. 

Therefore, to reduce the processing time, **BioMAaaS** dynamic resource allocation strategy that solves the issue by processing in parallel on a pool of virtual machines in a cloud environment. Moreover, our proposed solution also employs **privacy**, **customizability**, **scalability** to improve the efficiency of the service. 

It is an in-house setup that uses multiple threads to handle several client requests and performs VM scheduling to distribute the workload among the VMs. 

### BioMasS architecture
![client_server_architecture](https://user-images.githubusercontent.com/56432777/207242914-9d81d561-1284-43d5-b70f-b10201886bfc.png)

### WorkFlow from Client to Server (input) and Server to Client (Output)
![input_to_output](https://user-images.githubusercontent.com/56432777/207242942-b04314e8-b834-44ec-a575-f1bdbc9836ca.png)

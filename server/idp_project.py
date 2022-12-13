import sys
import threading
import time
import os
from os import listdir
from os.path import isfile, join
from MolLayers import engine
from Bio.PDB.mmtf import MMTFParser

def calc_confidential(file_path):
    data = engine.Layers(file_path)
    data.calc_surface(peel_layers=True)

def calculate(protein_id):
    structure = MMTFParser.get_structure_from_url(protein_id)
    data = engine.Layers(structure)
    data.calc_surface(peel_layers=True)

filename = sys.argv[1]
flag = sys.argv[2]
if flag=="2":
    file = open(filename, 'r')
    lines = file.readline().split()
    for i in lines[2:]:
        print(i)
        thread = threading.Thread(target=calc_confidential, args=(i,), name="t_"+i)
        thread.start()
        print("started")

file = open(filename, 'r')
lines = file.readline().split()
start = time.time()
for i in lines[2:]:
    print(i)
    thread = threading.Thread(target = calculate, args=(i,), name="t_"+i)
    thread.start()
    print("started")
end = time.time()
print(end-start)

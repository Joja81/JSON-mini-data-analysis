from tkinter import filedialog
from tkinter import *
import glob, os
import json

def load_files():
    root = Tk()
    root.withdraw()
    folder_selected = filedialog.askdirectory()

    oldpwd=os.getcwd()
    os.chdir(folder_selected)

    messages = []

    for file in glob.glob("*.json"):
        with open(file) as jsonFile:
            jsonObject = json.load(jsonFile)
            messages.extend(jsonObject['messages'])
    

    os.chdir(oldpwd)
    return messages
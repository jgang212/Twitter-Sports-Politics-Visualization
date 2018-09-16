import pandas as pd

geoTargets = pd.read_csv("Geotargets.csv", encoding = "ISO-8859-1");

stateNames = ["Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"]

states = []

for entry in geoTargets["Canonical Name"]:
    splitEntry = entry.split(",")
    appendState = ""
    for word in splitEntry:
        if word in stateNames:
            appendState = word
            break
    if appendState == "":
        print(splitEntry)
    states.append(appendState)
    
geoTargets["State"] = states

newgeoTargets = geoTargets[geoTargets["State"] != ""]

newgeoTargets.to_csv("NewGeotargets.csv", columns = ["Name","State"], index = False)

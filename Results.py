import pandas as pd

# import results
path = 'TweetResults.csv'
pathUniv = open(path, encoding="utf8")
tweetResults = pd.read_csv(pathUniv, sep='\t', header=None, error_bad_lines=False)
pathUniv.close()
tweetResults.columns = ['tweet', 'location', 'name', 'screenname', 'State', 'Name Gender', 'Politics', 'Sports']

# filter for tweets that had successful analyses
foundNameGender = tweetResults[tweetResults['Name Gender'] != 'Ambiguous']
foundState = tweetResults[tweetResults['State'] != 'NotAState']
foundPolitics = tweetResults[tweetResults['Politics'] > 0]
foundSports = tweetResults[tweetResults['Sports'] > 0]

print ("Found Name Gender:", len(foundNameGender), "-", "%.2f" % (len(foundNameGender)/len(tweetResults)*100), "%")
print ("Found State:", len(foundState), "-", "%.2f" % (len(foundState)/len(tweetResults)*100), "%")
print ("Found Politics:", len(foundPolitics), "-", "%.2f" % (len(foundPolitics)/len(tweetResults)*100), "%")
print ("Found Sports:", len(foundSports), "-", "%.2f" % (len(foundSports)/len(tweetResults)*100), "%")

# output 100 of each for manual annotation
foundNameGender.head(100).to_csv("NameGender.csv", header = ['name', 'screenname', 'Name Gender'], index = False, columns = ['name', 'screenname', 'Name Gender'])
foundState.head(100).to_csv("State.csv", header = ['location', 'State'], index = False, columns = ['location', 'State'])
tweetResults[tweetResults['State'] == 'NotAState'].head(100).to_csv("NotState.csv", header = ['location', 'State'], index = False, columns = ['location', 'State'])
foundPolitics.head(100).to_csv("Politics.csv", header = ['tweet', 'Politics'], index = False, columns = ['tweet', 'Politics'])
foundSports.head(100).to_csv("Sports.csv", header = ['tweet', 'Sports'], index = False, columns = ['tweet', 'Sports'])
tweetResults[tweetResults['Politics'] == 0].head(100).to_csv("NotPolitics.csv", header = ['tweet', 'Politics'], index = False, columns = ['tweet', 'Politics'])
tweetResults[tweetResults['Sports'] == 0].head(100).to_csv("NotSports.csv", header = ['tweet', 'Sports'], index = False, columns = ['tweet', 'Sports'])

# stats
print ("Politics, Ambiguous: ", len(foundPolitics[foundPolitics['Name Gender'] == 'Ambiguous']))
print ("Politics, Male: ", len(foundPolitics[foundPolitics['Name Gender'] == 'Male']))
print ("Politics, Female: ", len(foundPolitics[foundPolitics['Name Gender'] == 'Female']))
print ("--------")
print ("Sports, Ambiguous: ", len(foundSports[foundSports['Name Gender'] == 'Ambiguous']))
print ("Sports, Male: ", len(foundSports[foundSports['Name Gender'] == 'Male']))
print ("Sports, Female: ", len(foundSports[foundSports['Name Gender'] == 'Female']))
print ("--------")
print ("Ambiguous: ", len(tweetResults[tweetResults['Name Gender'] == 'Ambiguous']))
print ("Male: ", len(tweetResults[tweetResults['Name Gender'] == 'Male']))
print ("Female: ", len(tweetResults[tweetResults['Name Gender'] == 'Female']))
print ("--------")
print ("Top 10 states: ")
print (foundState['State'].value_counts().head(10))

# SVG output arrays
statePercs = foundState['State'].value_counts() / (len(foundState)) * 100
ambStatePercs = foundState[foundState['Name Gender'] == 'Ambiguous']['State'].value_counts() / (len(foundState[foundState['Name Gender'] == 'Ambiguous'])) * 100
maleStatePercs = foundState[foundState['Name Gender'] == 'Male']['State'].value_counts() / (len(foundState[foundState['Name Gender'] == 'Male'])) * 100
femaleStatePercs = foundState[foundState['Name Gender'] == 'Female']['State'].value_counts() / (len(foundState[foundState['Name Gender'] == 'Female'])) * 100
sportsStatePercs = foundState[foundState['Sports'] > 0]['State'].value_counts() / (len(foundState[foundState['Sports'] > 0])) * 100
politicsStatePercs = foundState[foundState['Politics'] > 0]['State'].value_counts() / (len(foundState[foundState['Politics'] > 0])) * 100

# SVG output function 

us_state_abbrev = {
    'Alabama': 'AL',
    'Alaska': 'AK',
    'Arizona': 'AZ',
    'Arkansas': 'AR',
    'California': 'CA',
    'Colorado': 'CO',
    'Connecticut': 'CT',
    'Delaware': 'DE',
    'Florida': 'FL',
    'Georgia': 'GA',
    'Hawaii': 'HI',
    'Idaho': 'ID',
    'Illinois': 'IL',
    'Indiana': 'IN',
    'Iowa': 'IA',
    'Kansas': 'KS',
    'Kentucky': 'KY',
    'Louisiana': 'LA',
    'Maine': 'ME',
    'Maryland': 'MD',
    'Massachusetts': 'MA',
    'Michigan': 'MI',
    'Minnesota': 'MN',
    'Mississippi': 'MS',
    'Missouri': 'MO',
    'Montana': 'MT',
    'Nebraska': 'NE',
    'Nevada': 'NV',
    'New Hampshire': 'NH',
    'New Jersey': 'NJ',
    'New Mexico': 'NM',
    'New York': 'NY',
    'North Carolina': 'NC',
    'North Dakota': 'ND',
    'Ohio': 'OH',
    'Oklahoma': 'OK',
    'Oregon': 'OR',
    'Pennsylvania': 'PA',
    'Rhode Island': 'RI',
    'South Carolina': 'SC',
    'South Dakota': 'SD',
    'Tennessee': 'TN',
    'Texas': 'TX',
    'Utah': 'UT',
    'Vermont': 'VT',
    'Virginia': 'VA',
    'Washington': 'WA',
    'West Virginia': 'WV',
    'Wisconsin': 'WI',
    'Wyoming': 'WY',
}

text_file = open("Blank_US_Map_With_Labels_new.svg", "r")
lines = text_file.readlines()
text_file.close()

def modifySVGString (percs, lines):
    newSVG = []
    for l in lines:
        newLine = l
        for state in percs.index:
            searchString = ">" + us_state_abbrev[state] + "<"
            if searchString in l:
                newLine = l.replace(searchString, ">" + str("%.2f" % percs[state]) + "%<")
                break
        newSVG.append(newLine)
    return newSVG

# state percentages
svg = modifySVGString(statePercs, lines)

new_file = open("state.svg", "w")
for item in svg:
    new_file.write("%s\n" % item)
new_file.close()

# ambiguous percentages
svg = modifySVGString(ambStatePercs, lines)

new_file = open("ambiguous.svg", "w")
for item in svg:
    new_file.write("%s\n" % item)
new_file.close()

# male percentages
svg = modifySVGString(maleStatePercs, lines)

new_file = open("male.svg", "w")
for item in svg:
    new_file.write("%s\n" % item)
new_file.close()

# female percentages
svg = modifySVGString(femaleStatePercs, lines)

new_file = open("female.svg", "w")
for item in svg:
    new_file.write("%s\n" % item)
new_file.close()

# sports percentages
svg = modifySVGString(sportsStatePercs, lines)

new_file = open("sports.svg", "w")
for item in svg:
    new_file.write("%s\n" % item)
new_file.close()

# politics percentages
svg = modifySVGString(politicsStatePercs, lines)

new_file = open("politics.svg", "w")
for item in svg:
    new_file.write("%s\n" % item)
new_file.close()

    
    
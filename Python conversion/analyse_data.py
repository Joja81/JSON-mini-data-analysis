import json
import re
import tokenize

from nltk.util import print_string
from load_files import load_files
import nltk
from datetime import datetime
import matplotlib.pyplot as plt




results = {
        'users' : {
            # 'User' : {
            #     'messages' : 0,
            #     'words' : 0,
            #     'characters' : 0
            # }
        }, # Dict with dicts for each user under name
        'times' : {}, # Store in 1 hour incremenets
        'word_use' : {
            # 'word' : num_of_uses
        }, # Word and num uses
    }


def main():

    messages = load_files()

    generate_times()

    lemmatizer = nltk.WordNetLemmatizer()

    for message in messages:
        if message['is_unsent'] == False:
            try:
                
                if 'content' not in message:
                    message['content'] = ""
                
                analyseMessage(message, lemmatizer)
            except Exception as e:
                print(e)
                print(message)

    remove_stop_words()

    output_results()

def generate_times():
    for idx in range(24):
        results['times'][idx] = 0

def output_results():
    results['word_use'] = dict(sorted(results['word_use'].items(), key=lambda item: -item[1]))

    with open('data.json', 'w') as f:
        json.dump(results, f, indent = 4)

    plt.plot(list(range(24)), results['times'].values())
    plt.show()
    
    sourceFile = open('response.txt', 'w')
    
    for user in results['users']:
        print(f"-------------{user}-------------", file = sourceFile)
        print(f"messages sent: {results['users'][user]['messages']}", file = sourceFile)
        print(f"words sent: {results['users'][user]['words']}", file = sourceFile)
        print(f"characters sent: {results['users'][user]['characters']}", file = sourceFile)
    
    print('\n\n\n -------------Word use-------------', file = sourceFile)
    print('Word\t\tNum uses', file = sourceFile)
    
    for word in results['word_use']:
        try:
            print(f"{word}:\t\t{results['word_use'][word]}", file = sourceFile)
        except:
            pass
    
    sourceFile.close()


def remove_stop_words():
    stop_words = open('stopWords.txt').read().splitlines()

    for stop_word in stop_words:
        results['word_use'][stop_word] = 0

    
def analyseMessage(message, lemmatizer):

    split_data = tokenize_message(message)

    record_user(message, split_data)

    record_words_use(split_data, lemmatizer)

    record_time(message)


def tokenize_message(message):
    
    message['content'] = message['content'].lower()
    
    
    
    tokens = message['content'].split()
    
    return tokens

def record_user(message, split_data):

    if not message['sender_name'] in results['users']:
        results['users'][message['sender_name']] = {
            'messages' : 0,
            'words' : 0,
            'characters': 0
        }

    results['users'][message['sender_name']]['messages'] += 1
    results['users'][message['sender_name']]['words'] += len(split_data)
    results['users'][message['sender_name']]['characters'] += len(message['content'])

def record_words_use(split_data, lemmatizer):
    for word in split_data:

        # word = lemmatizer.lemmatize(word)

        if word in results['word_use']:
            results['word_use'][word] += 1
        else:
            results['word_use'][word] = 1

def record_time(message):

    time = datetime.fromtimestamp(message['timestamp_ms']/1000)

    results['times'][time.hour] += 1
    
    


if __name__ == "__main__":
    main()
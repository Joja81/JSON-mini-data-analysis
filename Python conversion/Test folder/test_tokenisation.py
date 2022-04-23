import tokenize

with tokenize.open('hello.txt') as f:
    tokens = tokenize.generate_tokens(f.readline)
    for token in tokens:
        if token.type == 1:
            print(token.string)
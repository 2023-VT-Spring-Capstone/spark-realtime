from nltk.sentiment.vader import SentimentIntensityAnalyzer

new_words = {
    'citron': -4.0,  
    'hidenburg': -4.0,        
    'moon': 4.0,
    'highs': 2.0,
    'mooning': 4.0,
    'long': 2.0,
    'short': -2.0,
    'call': 4.0,
    'calls': 4.0,    
    'put': -4.0,
    'puts': -4.0,    
    'break': 2.0,
    'tendie': 2.0,
     'tendies': 2.0,
     'town': 2.0,     
     'overvalued': -3.0,
     'undervalued': 3.0,
     'buy': 4.0,
     'sell': -4.0,
     'gone': -1.0,
     'gtfo': -1.7,
     'paper': -1.7,
     'bullish': 3.7,
     'bearish': -3.7,
     'bagholder': -1.7,
     'stonk': 1.9,
     'green': 1.9,
     'money': 1.2,
     'print': 2.2,
     'rocket': 2.2,
     'bull': 2.9,
     'bear': -2.9,
     'pumping': -1.0,
     'sus': -3.0,
     'offering': -2.3,
     'rip': -4.0,
     'downgrade': -3.0,
     'upgrade': 3.0,     
     'maintain': 1.0,          
     'pump': 1.9,
     'hot': 1.5,
     'drop': -2.5,
     'rebound': 1.5,  
     'crack': 2.5
     }

upvoteRatio = 0.70

num_comm = 2

def classify_sentiment(title, body):
    compound_title = title['compound']
    compound_body = body['compound']
    compound = (compound_title+compound_body)/2
    if compound >= 0.05:
        return "bullish"
    elif compound <= -0.05:
        return "bearish"
    else:
        return "neutral"

def sentiment_analysis(post):
    vader = SentimentIntensityAnalyzer()
    vader.lexicon.update(new_words)
    if post['upvote_ratio'] >= upvoteRatio and post['num_comments'] >= num_comm: 
        score_title = vader.polarity_scores(post['title'])
        score_body = vader.polarity_scores(post['body'])
        score_neg = (score_title['neg']+score_body['neg'])/2
        score_neu = (score_title['neu']+score_body['neu'])/2
        score_pos = (score_title['pos']+score_body['pos'])/2
        score_compound = classify_sentiment(score_title, score_body) 
        if post['author'] is None:
            post['author'] = "unknown" 
        post_data = {
            "author": post['author'],
            "author_flair_text": post['author_flair_text'],
            "created_time": post['created_time'],
            "id": post['id'],
            "is_original": post['is_original'],
            "is_self": post['is_self'],
            "permalink": post['permalink'],
            "title": post['title'],
            "body": post['body'],
            "score": post['score'],
            "upvote_ratio": post['upvote_ratio'],
            "num_comments": post['num_comments'],
            "url": post['url'],
            "subreddit": post['subreddit'],
            "ticker": post['ticker'],
            "label": post['label'],
            "bullish":score_pos,
            "neutral":score_neu,
            "bearish":score_neg,
            "sentiment_labal":score_compound
        }
            

    return post_data
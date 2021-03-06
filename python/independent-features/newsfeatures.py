import feedparser
import re
#-----------------------------------------------------------------------------------------
feedlist = ['http://today.reuters.com/rss/topNews',
            'http://today.reuters.com/rss/domesticNews',
            'http://hosted.ap.org/lineups/TOPHEADS-rss_2.0.xml',
            'http://hosted.ap.org/lineups/USHEADS-rss_2.0.xml',
            'http://hosted.ap.org/lineups/WORLDHEADS-rss_2.0.xml',
            'http://hosted.ap.org/lineups/POLITICSHEADS-rss_2.0.xml',
            'http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml',
            'http://www.nytimes.com/services/xml/rss/nyt/International.xml',
            'http://news.google.com/?output=rss',
            'http://feeds.salon.com/salon/news',
            'http://www.foxnews.com/xmlfeed/rss/0,4313,0,00.rss',
            'http://www.foxnews.com/xmlfeed/rss/0,4313,80,00.rss',
            'http://www.foxnews.com/xmlfeed/rss/0,4313,81,00.rss',
            'http://rss.cnn.com/rss/edition.rss',
            'http://rss.cnn.com/rss/edition_world.rss',
            'http://rss.cnn.com/rss/edition_us.rss']
#-----------------------------------------------------------------------------------------
def strip_html(html):
    result = ''
    in_tag = False
    for c in html:
        if c == '<':
            in_tag = True
        elif c == '>':
            in_tag = False
            result += ' '
        elif not in_tag:
            result += c
    return result
#=========================================================================================
# Split words using whitespace as a separator.
# Return ony words 3+ characters long.
#=========================================================================================
def separate_words(text):
    splitter = re.compile('\\W*')
    return [s.lower() for s in splitter.split(text) if len(s) > 3]


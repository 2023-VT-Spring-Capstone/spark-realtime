import schedule
import time
from earnings import main as run_earnings
from info import main as run_info
from history import main as run_history
from holders import main as run_holders
from news import main as run_news
from share_count import main as run_share_count
from actions import main as run_actions
import traceback

schedule.every(1).days.do(run_earnings)
schedule.every(6).hours.do(run_info)
schedule.every(6).hours.do(run_history)
schedule.every(1).days.do(run_holders)
schedule.every(2).hours.do(run_news)
schedule.every(1).days.do(run_share_count)
# schedule.every(1).days.do(run_actions) // doesn't work Apr 12 2023

# schedule.every(10).seconds.do(run_earnings)
# schedule.every(10).seconds.do(run_info)
# schedule.every(10).seconds.do(run_history)
# schedule.every(10).seconds.do(run_holders)
# schedule.every(10).seconds.do(run_news)
# schedule.every(10).seconds.do(run_share_count)
# schedule.every(10).seconds.do(run_actions)

while True:
    try:
        print("Start scheduling task...")
        schedule.run_pending()
        print("End of scheduling task.")
    except Exception as e:
        print(f"Exception occurred: {e}")
        traceback.print_exc()
    time.sleep(7200)


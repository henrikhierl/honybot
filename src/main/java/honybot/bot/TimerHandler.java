package honybot.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimerHandler {

	// Map of all timed tasks by name
	private static Map<String, ScheduledFuture> timers = new HashMap<>();
	private static ScheduledThreadPoolExecutor timerPool = new ScheduledThreadPoolExecutor(4);
	
	static {
		timerPool.setRemoveOnCancelPolicy(true);
	}
	
	public static ScheduledFuture addOneShotTimer(String name, Runnable task, long delay, TimeUnit unit){
		System.out.println("added runnable for " + name);
		try{
			return timerPool.schedule(task, delay, unit);
		}catch(Exception e){
			
		}
		return null;
	}
	
	
	/**
	 * Adds a runnable to a ScheduledThreadPoolExecutor
	 * If a runnable with this name is already scheduled, it will be
	 * cancelled and removed, and then the new runnable will be added
	 *
	 * @param  name unique name of the runnable task
	 * @param  task runnable that will be scheduled
	 * @param  initialDelay delay before the first execution
	 * @param  period
	 * @param  unit Time unit of initialDelay and period
	 * @return returns true if the task was scheduled successfully, else false
	 */
	
	public static boolean addFixedRateTimer(String name, Runnable task, long initialDelay, long period, TimeUnit unit){
		System.out.println("added runnable for " + name);
		try{
			if(timers.containsKey(name)){
				removeAndCancelTimer(name);
			}
			ScheduledFuture future = timerPool.scheduleAtFixedRate(task, initialDelay, period, unit);
			timers.put(name, future);
			return true;
		}catch(Exception e){
			
		}
		return false;
	}
	
	/**
	 * Adds a runnable to a ScheduledThreadPoolExecutor
	 * If a runnable with this name is already scheduled, it will be
	 * cancelled and removed, and then the new runnable will be added
	 * There will be a fixed delay between the end of one execution and
	 * the beginning of the next one
	 *
	 * @param  name unique name of the runnable task
	 * @param  task runnable that will be scheduled
	 * @param  initialDelay delay before the first execution
	 * @param  period
	 * @param  unit Time unit of initialDelay and period
	 * @return returns true if the task was scheduled successfully, else false
	 */
	
	public static boolean addFixedDelayTimer(String name, Runnable task, long initialDelay, long period, TimeUnit unit){
		if(timers.containsKey(name)){
			removeAndCancelTimer(name);
		}
		ScheduledFuture future = timerPool.scheduleWithFixedDelay(task, initialDelay, period, unit);
		timers.put(name, future);
		return false;
	}

	
	public static void removeTimersByUsername(String username){
		ArrayList<String> keysToRemove = new ArrayList<>();
		for(String key : timers.keySet()){
			if(key.startsWith(username)){
				keysToRemove.add(key);
			}
		}
		for(String key : keysToRemove){
			removeAndCancelTimer(key);
		}
	}
	
	/**
	 * Checks if TimerHandler has a task scheduled with given name
	 * If timer exists, removes it from Map and cacnels it
	 * 
	 * @param name	name of the task to cancel
	 * @return	true if timer successfully cancelled, else false
	 */
	public static boolean removeAndCancelTimer(String name){
		try{
			if(timers.containsKey(name)){
				System.out.println("trying to remove runnable: " + name);
				ScheduledFuture timer = timers.remove(name);
				timer.cancel(false);
				System.out.println("Removed runnable: " + name);
				return true;
			}
		}catch(Exception e){
			
		}
		return false;
	}
	
	public static boolean hasTimer(String name){
		return timers.containsKey(name);
	}
}

package info.lemuu.jedis.handler;

import info.lemuu.jedis.credentials.RedisCredentials;
import info.lemuu.jedis.handler.listener.JedisListener;
import info.lemuu.jedis.handler.reflection.JedisInvoke;
import info.lemuu.jedis.thread.RedisThread;

import java.util.LinkedList;
import java.util.List;

public abstract class RedisListener extends RedisThread {

	private boolean channelsSubscribed;
	private final static List<JedisListener> listeners = new LinkedList<JedisListener>();
	
	public RedisListener(RedisCredentials redisCredentials) {
		super(redisCredentials);
	}

	public void registerListener(JedisListener jedisListener) {
		RedisListener.listeners.add(jedisListener);
	}
	
	public void unregisterListener(JedisListener jedisListener) {
		RedisListener.listeners.remove(jedisListener);
	}

	protected static void callEvent(String channel, String message) {
		new Thread(()-> {
			RedisListener.listeners.forEach(jedisListener -> {
				JedisInvoke jedisInvoke = new JedisInvoke(jedisListener, channel, message);
				jedisInvoke.invoke();
			});
		}).start();
	}

	public boolean isChannelsSubscribed() {
		return channelsSubscribed;
	}
	public void setChannelsSubscribed(boolean channelsSubscribed) {
		this.channelsSubscribed = channelsSubscribed;
	}

}
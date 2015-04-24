package org.github.rusnavu.events;

public interface IEventListener {

	void handleEvent(String eventType, Object eventArg);

}

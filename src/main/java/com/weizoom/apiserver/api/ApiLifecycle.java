package com.weizoom.apiserver.api;

public class ApiLifecycle {

	public static enum State {
		INITIALIZED, 
		STARTED, 
		CLOSED
	}

	private volatile State state = State.INITIALIZED;

	public State state() {
		return this.state;
	}

	public boolean initialized() {
		return state == State.INITIALIZED;
	}

	public boolean started() {
		return state == State.STARTED;
	}

	public boolean closed() {
		return state == State.CLOSED;
	}

	public boolean canMoveToStarted() throws IllegalApiStateException {
		State localState = this.state;
		if (localState == State.INITIALIZED) {
			return true;
		}
		
		if (localState == State.STARTED) {
			return false;
		}
		
		if (localState == State.CLOSED) {
			throw new IllegalApiStateException("Can't move to started state when closed");
		}
		
		throw new IllegalApiStateException("Can't move to started with unknown state");
	}

	public boolean moveToStarted() throws IllegalApiStateException {
		State localState = this.state;
		if (localState == State.INITIALIZED) {
			state = State.STARTED;
			return true;
		}
		
		if (localState == State.STARTED) {
			return false;
		}
		
		if (localState == State.CLOSED) {
			throw new IllegalApiStateException("Can't move to started state when closed");
		}
		
		throw new IllegalApiStateException("Can't move to started with unknown state");
	}

	public boolean canMoveToClosed() throws IllegalApiStateException {
		State localState = state;
		
		if (localState == State.INITIALIZED || localState == State.STARTED) {
			return true;
		}
		
		if (localState == State.CLOSED) {
			return false;
		}
		
		throw new IllegalApiStateException("Can't move to closed with unknown state");
	}

	public boolean moveToClosed() throws IllegalApiStateException {
		State localState = this.state;
		if (localState == State.INITIALIZED || localState == State.STARTED) {
			state = State.CLOSED;
			return true;
		}
		
		if (localState == State.CLOSED) {
			return false;
		}
		
		throw new IllegalApiStateException("Can't move to closed with unknown state");
	}

	@Override
	public String toString() {
		return state.toString();
	}
	
	public ApiLifecycle clone() {
		ApiLifecycle copy = new ApiLifecycle();
		copy.state = this.state;
		return copy;
	}

}

package week34;

public class ChandyLamportInitiator extends ChandyLamportProcess {

	@Override
	public void init() {
		if (!snapshotInitiated) {
			snapshotInitiated = true;
			startSnapshot();
			recordLocalState();
			sendMarkerMessages();
		}
	}
}

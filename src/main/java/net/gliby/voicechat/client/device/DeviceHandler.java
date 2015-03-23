package net.gliby.voicechat.client.device;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import net.gliby.voicechat.client.sound.SoundManager;

public class DeviceHandler {

	private List<Device> devices = new ArrayList<Device>();

	public Device getDefaultDevice() {
		TargetDataLine line;
		final DataLine.Info info = new DataLine.Info(TargetDataLine.class, SoundManager.getUniversalAudioFormat());
		if (!AudioSystem.isLineSupported(info)) {
			return null;
		}
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
		} catch (final Exception ex) {
			return null;
		}
		if (line != null) return getDeviceByLine(line);
		return null;
	}

	private Device getDeviceByLine(TargetDataLine line) {
		for (int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			if (device.getLine().getLineInfo().equals(line.getLineInfo())) {
				return device;
			}
		}
		return null;
	}

	public Device getDeviceByName(String deviceName) {
		for (int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			if (device.getName().equals(deviceName)) {
				return device;
			}
		}
		return null;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public boolean isEmpty() {
		return devices.isEmpty();
	}

	public List<Device> loadDevices() {
		devices.clear();
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixers) {
			Mixer mixer = AudioSystem.getMixer(info);
			try {
				javax.sound.sampled.DataLine.Info tdlLineInfo = new DataLine.Info(TargetDataLine.class, SoundManager.getUniversalAudioFormat());
				TargetDataLine tdl = (TargetDataLine) mixer.getLine(tdlLineInfo);
				if (info != null) devices.add(new Device(tdl, info));
			} catch (LineUnavailableException e) {
			} catch (IllegalArgumentException e) {
			}
		}
		return devices;
	}
}
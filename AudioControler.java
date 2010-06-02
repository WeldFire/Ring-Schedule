package rom.Ring.Schedule;

import android.content.Context;
import android.media.AudioManager;

public class AudioControler{

		int previousRingerMode;
		int streamVolume;
		AudioManager mAudioManager; 
		Context zContext;
		
		public AudioControler(Context c) 
		{
			if(zContext == null)
			{
				zContext = c;
			}
			String as = Context.AUDIO_SERVICE;
			mAudioManager = (AudioManager) zContext.getSystemService(as);
			streamVolume = mAudioManager.getStreamVolume(1);
		}
		public void setPrevRing()
		{
			previousRingerMode = mAudioManager.getRingerMode();
		}
		public void toggleVib()
		{		
			if(mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE)
			{
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
		}
		
		public void toggleSilent()
		{
			if(mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
			{
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
		}
		
		public void toggleNorm()
		{
			if(mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			{
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
		}
		
		public void maxVol()
		{	
			int max = mAudioManager.getStreamMaxVolume(1);
			while(mAudioManager.getStreamVolume(1) < max)
			{
				mAudioManager.adjustStreamVolume(1, 1, 0);
			}
		}
		
		public void recover()
		{
			mAudioManager.setRingerMode(previousRingerMode);
			
			if(mAudioManager.getStreamVolume(1) == mAudioManager.getStreamMaxVolume(1))
			{
				while(mAudioManager.getStreamVolume(1) > streamVolume)
				{
					mAudioManager.setStreamVolume(1, -1, 0);
				}	
			}
		}
}
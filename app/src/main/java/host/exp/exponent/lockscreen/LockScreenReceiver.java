package host.exp.exponent.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LockScreenReceiver extends BroadcastReceiver {
    //Member Variable
    private TelephonyManager telephonyManager   =   null;
    private boolean isPhoneIdle                 =   true;

    private PhoneStateListener phoneListener    =   new PhoneStateListener()
    {
        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        { //Member Object
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE :
                    isPhoneIdle = true;
                    break;
                case TelephonyManager.CALL_STATE_RINGING :
                    isPhoneIdle = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK :
                    isPhoneIdle = false;
                    break;
            }
        }
    };



    //Constructor
    public LockScreenReceiver() {
    }



    //Override Method
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            if(telephonyManager == null)
            {
                telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }


            if(isPhoneIdle)
            {
                Intent i = new Intent(context, LockScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }


        }
    }
}

package com.example.hellomindwave;

import java.util.concurrent.ExecutionException;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HelloMindWaveActivity extends Activity {
	
	//Declaración de variables
	BluetoothAdapter bluetoothAdapter;
	TextView tMeditation, tAttention, tBlink, tOthers;
	ImageView iBlink;
	TGDevice tgDevice;
	
	final boolean rawEnabled = false;
	boolean closedEye = false;
	
	
	//Método onCreate. Este método inicializa la "Activity.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello_mind_wave);
		
		// Asociamos los elementos de la interfaz con nuestras variables de programa
		tAttention = (TextView)findViewById(R.id.textViewAttention);
		tMeditation = (TextView)findViewById(R.id.textViewMeditation);
		tBlink = (TextView)findViewById(R.id.textViewBlink);
		iBlink = (ImageView)findViewById(R.id.imageViewBlink);
		tOthers = (TextView)findViewById(R.id.textViewInfo2);
     
		//Obtenemos el bluethooth por defecto
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if(bluetoothAdapter == null) {
        	// Alerta al usuario de que el bluetooth no está disponible
        	Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }else {
        	// Creamos el TGDevice //
        	tgDevice = new TGDevice(bluetoothAdapter, handler);
        }  
        
        //Si el dispositivo se encuentra en el estado adecuado, entonces conecta.
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(rawEnabled);  
	}
	
	// onDestroy() es llamado cuando la actividad es destruida.
	// Cerramos en él la conexión con el dispositivo.
	@Override
	public void onDestroy() {
		tgDevice.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hello_mind_wave, menu);
		return true;
	}
	
    /**
     * Manejador de mensajes del TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:

                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	tOthers.setText("Connecting...");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	tOthers.setText("Connected.\n");
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	tOthers.setText("Can't find\n");
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	tOthers.setText("not paired\n"); 
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	tOthers.setText("Disconnected mang\n");
                }

                break;
            case TGDevice.MSG_POOR_SIGNAL:
            		//signal = msg.arg1;
            	tOthers.setText("PoorSignal: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_RAW_DATA:	  
            		//raw1 = msg.arg1;
            		//tv.append("Got raw: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_HEART_RATE:
            	tOthers.setText("Heart rate: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_ATTENTION:
            		//att = msg.arg1;
            	tAttention.setText("Attention: " + msg.arg1 + "\n");
            		//Log.v("HelloA", "Attention: " + att + "\n");
            	break;
            case TGDevice.MSG_MEDITATION:
            	tMeditation.setText("Meditation: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_BLINK:
            	if (msg.arg1 <= 50) {
    				iBlink.setImageResource(R.drawable.green_eye);
    			} else if (msg.arg1 <= 100) {
    				iBlink.setImageResource(R.drawable.blue_eye);
    			} else{
    				iBlink.setImageResource(R.drawable.red_eye);
    			}
          
            	tBlink.setText(String.valueOf(msg.arg1));
            	new SleepTask().execute(300);
            	break;
            case TGDevice.MSG_RAW_COUNT:
            		//tv.append("Raw Count: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            case TGDevice.MSG_RAW_MULTI:
            	//TGRawMulti rawM = (TGRawMulti)msg.obj;
            	//tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            default:
            	break;
        }
        }
    };
    
    
    public void doStuff(View view) {
    	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(rawEnabled);   
    	//tgDevice.ena
    }
    
	public class SleepTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				Thread.sleep(params[0]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			iBlink.setImageResource(R.drawable.white_eye);
		}

	}
}
    
  
   


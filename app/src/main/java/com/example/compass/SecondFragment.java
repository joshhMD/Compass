package com.example.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SecondFragment extends Fragment implements SensorEventListener
{

    private ImageView imageview;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float direction = 0f;
    private float currentDirection = 0f;
    private SensorManager mSensorManager;


    Button back;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        back = (Button)view.findViewById(R.id.back_button);
        imageview = (ImageView) view.findViewById(R.id.compassImage);
        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);


        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        final float alpha = .97f;
        synchronized (this)
        {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                gravity[0] = alpha*gravity[0]+(1-alpha)*event.values[0];
                gravity[1] = alpha*gravity[1]+(1-alpha)*event.values[1];
                gravity[2] = alpha*gravity[2]+(1-alpha)*event.values[2];
            }

            if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                geomagnetic[0] = alpha*geomagnetic[0]+(1-alpha)*event.values[0];
                geomagnetic[1] = alpha*geomagnetic[1]+(1-alpha)*event.values[1];
                geomagnetic[2] = alpha*geomagnetic[2]+(1-alpha)*event.values[2];
            }

            float x[] = new float[9];
            float y[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(x,y,gravity,geomagnetic);

            if(success)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(x,orientation);
                direction = (float)Math.toDegrees(orientation[0]);
                direction = (direction + 360)%360;

                Animation anim = new RotateAnimation(-currentDirection, -direction, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                currentDirection = direction;
                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                imageview.startAnimation(anim);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}

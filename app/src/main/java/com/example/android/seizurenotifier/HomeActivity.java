    package com.example.android.seizurenotifier;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

    public class HomeActivity extends AppCompatActivity {

        private List<Seizure> seizureList;
        private TextView mTextView;
        private TextView mLockStateView;
        private int waveOutCount=0;
        private int waveInCount=0;

        private String[] ydata = {"Alcohol/Drugs","Flashlights","Lack of Sleep","Lack of Medication"
        ,"Stress/Anxiety"};

        private float[] xData = {5,2,7,2,4};

        PieChart pieChart;

        private DeviceListener mListener = new AbstractDeviceListener() {

            // onConnect() is called whenever a Myo has been connected.


            @Override
            public void onConnect(Myo myo, long timestamp) {
                // Set the text color of the text view to cyan when a Myo connects.

                mLockStateView.setText("Your Myo is connected");
                myo.unlock(Myo.UnlockType.HOLD);
            }

            // onDisconnect() is called whenever a Myo has been disconnected.
            @Override
            public void onDisconnect(Myo myo, long timestamp) {
                // Set the text color of the text view to red when a Myo disconnects.
                mTextView.setTextColor(Color.RED);
            }

            // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
            // arm. This lets Myo know which arm it's on and which way it's facing.
            @Override
            public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
                mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
            }

            // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
            // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
            // when Myo is moved around on the arm.
            @Override
            public void onArmUnsync(Myo myo, long timestamp) {
                mTextView.setText(R.string.hello_world);
            }

            // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
            // policy, that means poses will now be delivered to the listener.
            @Override
            public void onUnlock(Myo myo, long timestamp) {

            }

            // onLock() is called whenever a synced Myo has been locked. Under the standard locking
            // policy, that means poses will no longer be delivered to the listener.
            @Override
            public void onLock(Myo myo, long timestamp) {
                mLockStateView.setText(R.string.locked);
                myo.unlock(Myo.UnlockType.HOLD);
            }

            // onOrientationData() is called whenever a Myo provides its current orientation,
            // represented as a quaternion.
            @Override
            public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
                // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
                myo.unlock(Myo.UnlockType.HOLD);
                float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
                float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
                float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
                // Adjust roll and pitch for the orientation of the Myo on the arm.
            }

            // onPose() is called whenever a Myo provides a new pose.
            @Override
            public void onPose(Myo myo, long timestamp, Pose pose) {
                // Handle the cases of the Pose enumeration, and change the text of the text view
                // based on the pose we receive.
                switch (pose) {
                    case UNKNOWN:
                        mTextView.setText(getString(R.string.hello_world));
                        break;
                    case REST:
                    case DOUBLE_TAP:
                        int restTextId = R.string.hello_world;
                        switch (myo.getArm()) {
                            case LEFT:
                                restTextId = R.string.arm_left;
                                break;
                            case RIGHT:
                                restTextId = R.string.arm_right;
                                break;
                        }
                        mTextView.setText(getString(restTextId));
                        break;
                    case FIST:
                        mTextView.setText(getString(R.string.pose_fist));
                        break;
                    case WAVE_IN:
                        mTextView.setText(getString(R.string.pose_wavein));
                        waveInCount++;

                        break;
                    case WAVE_OUT:
                        mTextView.setText(getString(R.string.pose_waveout));
                        waveOutCount++;
                        if(waveInCount<3||waveInCount>3)waveInCount=0;

                        if(waveOutCount>2)waveOutCount=0;
                        if(waveInCount==3&&waveOutCount==2){
                            Toast.makeText(HomeActivity.this,"Emergency alert Message has been sent to " +
                                    "Santhosh Ramachadran",Toast.LENGTH_SHORT).show();

                            PendingIntent sentIntent = null, deliveryIntent = null;
                            SmsManager smsManager= SmsManager.getDefault();
                            smsManager.sendTextMessage("12019205212",null,"!!Help I'm having a seizure"
                                    +"I'm at 38.971500,-76.940310 !!",
                                    sentIntent,deliveryIntent);

                            waveOutCount=0;
                            waveInCount=0;
                        }
                        break;


                    case FINGERS_SPREAD:
                        mTextView.setText(getString(R.string.pose_fingersspread));
                        break;
                }
                myo.notifyUserAction();
/*
            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.HOLD);
            }
            */
            }
        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pieChart = findViewById(R.id.pieChart);

        mTextView = findViewById(R.id.pose_status);
        mLockStateView = findViewById(R.id.connection_status);



        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setCenterText("Seizure Trigger Data");
        pieChart.setCenterTextSize(10);
        pieChart.setDrawEntryLabels(true);

        addDataSet(pieChart);

        GraphView graph =  findViewById(R.id.graph);
        DataPoint[] dataPoints = new DataPoint[]{
                new DataPoint(1, 1),
                new DataPoint(2, 5),
                new DataPoint(3, 3),
                new DataPoint(4, 2),
                new DataPoint(5,1),
                new DataPoint(6,2),
                new DataPoint(7,3)
        };
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
        Date d = new Date();
        Date d5 = new Date();
        Date d6 = new Date();
        Date d7 = new Date();

        Hub hub = Hub.getInstance();

        hub.setLockingPolicy(Hub.LockingPolicy.NONE);
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);

        Seizure seizure = new Seizure("It was not so good","3 mins",
                "Medication","12:45 am","12:48am","none",
                false,"12345","10/21/18");
        seizureList = new ArrayList<>();
        seizureList.add(seizure);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1),
                new DataPoint(2, 5),
                new DataPoint(3, 3),
                new DataPoint(4, 2),
                new DataPoint(5,1),
                new DataPoint(6,2),
                new DataPoint(7,3)
        });
        graph.addSeries(series);


        graph.addSeries(series2);
        series2.setShape(PointsGraphSeries.Shape.POINT);


        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"12:45am", "12:55pm", "1:00pm","1:25pm","1:45pm","2:00pm","2:15pm"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


        series2.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Intent intent = new Intent(HomeActivity.this,SeizureActivity.class);
                Toast.makeText(HomeActivity.this,dataPoint.getX()+"",Toast.LENGTH_SHORT).show();
                for(Seizure s: seizureList){
                    if(s.getId().equals("12345")){

                        intent.putExtra("description",s.getDesc());
                        intent.putExtra("duration",s.getDuration());
                        intent.putExtra("startTime",s.getStartTime());
                        intent.putExtra("endTime",s.getEndTime());
                        intent.putExtra("possibleTriggers",s.getPossibleTriggers());
                        intent.putExtra("injuries",s.getInjuries());
                        intent.putExtra("isEmergency",s.isEmergency());
                        intent.putExtra("date",s.getDate());
                    }
                }
                startActivity(intent);
            }
        });

    }

    public void addDataSet(PieChart pieChart){
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i=0;i<xData.length;i++){
            PieEntry pieEntry = new PieEntry(xData[i],i);
            pieEntry.setLabel(ydata[i]);
            yEntrys.add(pieEntry);
        }

        for(int i=0;i<ydata.length;i++){
            xEntrys.add(ydata[i]);
        }

        PieDataSet pieDataSet = new PieDataSet(yEntrys,"Trigger Data");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setFormSize(10f); // set the size of the legend forms/shapes
        legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(5f); // set the space between the legend entries on the y-axis



        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // We don't want any callbacks when the Activity is gone, so unregister the listener.
            Hub.getInstance().removeListener(mListener);

            if (isFinishing()) {
                // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
                Hub.getInstance().shutdown();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (R.id.action_scan == id) {
                onScanActionSelected();
                return true;
            }
            else if(R.id.set_priority==id){

            }
            return super.onOptionsItemSelected(item);
        }

        private void onScanActionSelected() {
            // Launch the ScanActivity to scan for Myos to connect to.
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        }
}

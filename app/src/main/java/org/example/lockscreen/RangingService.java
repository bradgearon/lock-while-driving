package org.example.lockscreen;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class RangingService extends Service implements BeaconConsumer {
  private BeaconManager beaconManager;
  private static float range = 20f;

  public static boolean isInRange() {
    boolean isInRange = range < 15f;
    Log.d("com.wds.untitled2", "range: " + range + " is in range " + isInRange);
    return isInRange;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("com.wds.untitled2", "on create ranging service");

    int permissionCheck = ContextCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_COARSE_LOCATION);
    Log.d("com.wds.untitled2", "access coarse location: " + permissionCheck);

    beaconManager = BeaconManager.getInstanceForApplication(this);
    // To detect proprietary beacons, you must add a line like below corresponding to your beacon
    // type.  Do a web search for "setBeaconLayout" to get the proper expression.
    // beaconManager.getBeaconParsers().add(new BeaconParser().
    //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
   /* beaconManager.getBeaconParsers().add(new BeaconParser().
      setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
    beaconManager.getBeaconParsers().add(new BeaconParser().
      setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
    beaconManager.getBeaconParsers().add(new BeaconParser().
      setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
    beaconManager.getBeaconParsers().add(new BeaconParser().
      setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
    beaconManager.getBeaconParsers().add(new BeaconParser().
      setBeaconLayout("s:0-1=fed8,m:2-2=00,p:3-3:-41,i:4-21v"));
    beaconManager.getBeaconParsers().add(new BeaconParser()
      .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));*/

    beaconManager.getBeaconParsers().clear();
    beaconManager.getBeaconParsers()
      .add(new BeaconParser() {
        @Override
        protected Beacon fromScanData(byte[] bytesToProcess, int rssi, BluetoothDevice device, Beacon beacon) {
          bytesToProcess[0] = 0x1;
          bytesToProcess[1] = -0x1;
          return super.fromScanData(bytesToProcess, rssi, device, beacon);
        }
      }.setBeaconLayout("m:0-1=060b,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));


    beaconManager.bind(this);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    beaconManager.unbind(this);
    return super.onUnbind(intent);
  }

  @Override
  public void onBeaconServiceConnect() {
    Identifier myBeaconNamespaceId = Identifier.parse("6c656e64-4d69-6372-6f11-071e948df148");
    Identifier myBeaconInstanceId = Identifier.parse("12692");

    Region region = new Region("my-beacon-region", myBeaconNamespaceId, myBeaconInstanceId, null);

    beaconManager.addRangeNotifier(new RangeNotifier() {
      @Override
      public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
          double distance = beacons.iterator()
            .next()
            .getDistance();
          double inFeet = distance * 3.2808 * 100;
          range = (float) inFeet;
        }
      }
    });

    try {

      beaconManager.startRangingBeaconsInRegion(region);
    } catch (RemoteException ignored) {

    }
  }
}
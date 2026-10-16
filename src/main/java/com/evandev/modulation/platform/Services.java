package com.evandev.modulation.platform;

import com.evandev.modulation.platform.services.IPlatformHelper;

//? if fabric {
/*import com.evandev.modulation.fabric.platform.FabricPlatformHelper;
*///?} else {
import com.evandev.modulation.neoforge.platform.NeoForgePlatformHelper;
//?}

public class Services {
    //? if fabric {
    /*public static final IPlatformHelper PLATFORM = new FabricPlatformHelper();
    *///?} else {
    public static final IPlatformHelper PLATFORM = new NeoForgePlatformHelper();
    //?}
}

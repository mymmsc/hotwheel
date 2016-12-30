package org.hotwheel.utils;

import org.hotwheel.util.manifests.ClasspathMfs;
import org.hotwheel.util.manifests.Manifests;

import java.io.IOException;

/**
 * Created by wangfeng on 2016/12/30.
 */
public class TestJarVersion {
    public static void main(String[] args) throws IOException {
        Manifests manifests = new Manifests();
        manifests.append(new ClasspathMfs());
        String version = manifests.read("hotwheel-asio-Version");
        System.out.println("version is " + version);
    }
}

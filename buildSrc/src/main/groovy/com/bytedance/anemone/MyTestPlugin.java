package com.bytedance.anemone;


import org.gradle.api.*;

public class MyTestPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
//        System.out.println("MyTestPlugin apply");
        project.task("MyTestPlugin");
    }
}

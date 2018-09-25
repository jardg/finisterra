package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.model.descriptors.*;
import ar.com.tamborindeguy.model.readers.AODescriptorsReader;
import ar.com.tamborindeguy.model.readers.DescriptorsReader;
import ar.com.tamborindeguy.model.readers.GenericReader;
import ar.com.tamborindeguy.model.serializers.BodyDescriptorSerializer;
import ar.com.tamborindeguy.model.serializers.GraphicsSerializer;
import ar.com.tamborindeguy.model.Graphic;
import ar.com.tamborindeguy.model.Objects;
import ar.com.tamborindeguy.objects.types.Obj;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptorsHandler {

    private static Map<Integer, Graphic> graphics;
    //    private static List<BodyDescriptor> bodies;
    private static Map<Integer, BodyDescriptor> bodies;
    private static List<HeadDescriptor> heads;
    private static List<HelmetDescriptor> helmets;
    private static List<WeaponDescriptor> weapons;
    private static List<ShieldDescriptor> shields;
    private static List<FXDescriptor> fxs;
    private static Objects objects;
    private static DescriptorsReader reader = new AODescriptorsReader();

    public static void load() {
        graphics = new GenericReader<Graphic>().read("graficos", Graphic.class, new GraphicsSerializer(), Graphic::getGrhIndex);
        bodies = new GenericReader<BodyDescriptor>().read("cuerpos", BodyDescriptor.class, new BodyDescriptorSerializer(), Descriptor::getId);
        weapons = load("weapons2");
        shields = load("shields2");
        heads = load("heads2");
        helmets = load("helmets2");
        fxs = load("fxs2");
        objects = reader.loadObjects("obj");
    }

    public static List load(String fileName) {
        Json json = getJson();
        FileHandle file = Gdx.files.internal("data/descriptors/" + fileName + ".json");
        return (List) json.fromJson(ArrayList.class, file);
    }

    public static Map loadMap(String fileName) {
        Json json = getJson();
        FileHandle file = Gdx.files.internal("data/descriptors/" + fileName + ".json");
        return (java.util.HashMap) json.fromJson(HashMap.class, file);
    }

    private static Json getJson() {
        Json json = new Json();
        json.addClassTag("graphics", Graphic.class);
        json.addClassTag("bodies", BodyDescriptor.class);
        json.addClassTag("heads", HeadDescriptor.class);
        json.addClassTag("helmets", HelmetDescriptor.class);
        json.addClassTag("weapons", WeaponDescriptor.class);
        json.addClassTag("shields", ShieldDescriptor.class);
        json.addClassTag("fxs", FXDescriptor.class);
        return json;
    }

    public static ArrayList read(String fileName, Class type, Json.Serializer serializer) {
        FileHandle file = Gdx.files.internal("data/indices/" + fileName + ".json");
        Json json = new Json();
        json.setSerializer(type, serializer);

        return json.fromJson(ArrayList.class, type, file);
    }


    public static Map<Integer, Graphic> getGraphics() {
        return graphics;
    }

    public static Map<Integer, BodyDescriptor> getBodies() {
        return bodies;
    }

    public static List<FXDescriptor> getFxs() {
        return fxs;
    }

    public static List<HeadDescriptor> getHeads() {
        return heads;
    }

    public static List<HelmetDescriptor> getHelmets() {
        return helmets;
    }

    public static List<ShieldDescriptor> getShields() {
        return shields;
    }

    public static List<WeaponDescriptor> getWeapons() {
        return weapons;
    }

    public static BodyDescriptor getBody(int index) {
        return DescriptorsHandler.getBodies().get(index);
    }

    public static HeadDescriptor getHead(int index) {
        return DescriptorsHandler.getHeads().get(index);
    }

    public static HelmetDescriptor getHelmet(int index) {
        return DescriptorsHandler.getHelmets().get(index);
    }

    public static FXDescriptor getFX(int index) {
        return DescriptorsHandler.getFxs().get(index);
    }

    public static ShieldDescriptor getShield(int index) {
        return DescriptorsHandler.getShields().get(index);
    }

    public static WeaponDescriptor getWeapon(int index) {
        return DescriptorsHandler.getWeapons().get(index);
    }

    public static Graphic getGraphic(int index) {
        return DescriptorsHandler.getGraphics().get(index);
    }

    public static Obj getObject(int index) {
        return objects.getObject(index).get();
    }

}

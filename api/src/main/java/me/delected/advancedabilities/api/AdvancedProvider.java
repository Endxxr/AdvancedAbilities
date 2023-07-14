package me.delected.advancedabilities.api;

public class AdvancedProvider {

    private static AdvancedAPI api;

    public static void setApi(AdvancedAPI api) {
        AdvancedProvider.api = api;
    }

    public static AdvancedAPI getAPI() {
        return api;
    }


}

package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.grid.HexGrid;
import com.gordonfromblumberg.games.core.common.grid.HexGridBuilder;
import com.gordonfromblumberg.games.core.common.grid.HexRow;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.common.utils.RandomGen;

public class MainWorld extends World {
    static final int hexWidth;
    static final int hexHeight;
    static final float hexIncline;
    static final float defaultWeight = 3f;
    static final float roadWeight = 1f;
    public static final Array<Algorithm> algorithms = new Array<>();

    static {
        final ConfigManager config = AbstractFactory.getInstance().configManager();
        hexWidth = config.getInteger("hexWidth");
        hexHeight = config.getInteger("hexHeight");
        hexIncline = config.getFloat("hexIncline");

        algorithms.add(StraightforwardAlgorithm.instance());
    }

    final MainWorldParams params = new MainWorldParams();
    final Array<Hex> emptyHexes = new Array<>();
    final ObjectSet<Hex> passableHexes = new ObjectSet<>();
    final Array<Hex> cities = new Array<>();

    HexGrid grid;
    boolean gridCreated = false;

    private Algorithm algorithm = StraightforwardAlgorithm.instance();

    private float time;
    private float updateDelay;
    private boolean finished;
    private boolean running;

    public MainWorldParams getParams() {
        return params;
    }

    public void createGrid() {
        grid = HexGridBuilder.start()
                .rect(params.width,params.height)
                .hexParams(hexWidth, hexHeight, hexIncline)
                .weight(defaultWeight)
                .build();
        gridCreated = true;

        fillLists();

        generateCities();
        generateObstacles();

        algorithm.reset();
    }

    @Override
    public void update(float delta, float mouseX, float mouseY) {
        super.update(delta, mouseX, mouseY);

        if (!paused && running) {
            time += delta;
            if (time < updateDelay * algorithm.getStepDelayCoef()) {
                return;
            }

            time = 0;

            if (!finished)
                finished = algorithm.step(this);
        }
    }

    public void run() {
        running = true;
        finished = false;
    }

    private void generateCities() {
        int n = params.cityCount;

        while (n-- > 0) {
            Hex hex = RandomGen.INSTANCE.getRandomItem(emptyHexes);
            hex.setObject(HexType.city);
            emptyHexes.removeValue(hex, true);
            cities.add(hex);
        }
    }

    private void generateObstacles() {
        int n = params.obstacleLevel * grid.getHexCount() / 16;

        while (n-- > 0) {
            Hex hex = RandomGen.INSTANCE.getRandomItem(emptyHexes);
            hex.setObject(HexType.obstacle);
            grid.removeEdges(hex);
            emptyHexes.removeValue(hex, true);
            passableHexes.remove(hex);
        }
    }

    private void clearLists() {
        emptyHexes.clear();
        passableHexes.clear();
        cities.clear();
    }

    private void cleanGrid() {
        clearLists();
        for (final HexRow row : grid) {
            for (final Hex hex : row) {
                hex.setObject(null);
            }
        }
    }

    private void fillLists() {
        clearLists();
        for (final HexRow row : grid) {
            for (final Hex hex : row) {
                emptyHexes.add(hex);
                passableHexes.add(hex);
            }
        }
    }

    public void setStepsPerSec(int steps) {
        updateDelay = 1f / steps;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
}

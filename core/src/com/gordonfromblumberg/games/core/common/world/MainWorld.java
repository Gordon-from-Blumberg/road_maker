package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.graph.Edge;
import com.gordonfromblumberg.games.core.common.grid.Hex;
import com.gordonfromblumberg.games.core.common.grid.HexGrid;
import com.gordonfromblumberg.games.core.common.grid.HexGridBuilder;
import com.gordonfromblumberg.games.core.common.grid.HexRow;
import com.gordonfromblumberg.games.core.common.log.LogManager;
import com.gordonfromblumberg.games.core.common.log.Logger;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.common.utils.RandomGen;

public class MainWorld extends World {
    private static final Logger log = LogManager.create(MainWorld.class);
    private static final String LAST_USED_CONFIG_KEY = "last-used-config";

    static final int hexWidth;
    static final int hexHeight;
    static final float hexIncline;
    static final float defaultWeight = 3f;
    static final float roadWeight = 1f;

    static {
        final ConfigManager config = AbstractFactory.getInstance().configManager();
        hexWidth = config.getInteger("hexWidth");
        hexHeight = config.getInteger("hexHeight");
        hexIncline = config.getFloat("hexIncline");
    }

    public final Array<Algorithm> algorithms = new Array<>();

    final MainWorldParams params = new MainWorldParams();
    final Array<Hex> emptyHexes = new Array<>();
    final ObjectSet<Hex> passableHexes = new ObjectSet<>();
    final Array<Hex> cities = new Array<>();

    HexGrid grid;
    boolean gridCreated = false;

    private Algorithm algorithm;

    private float time;
    private float updateDelay = 1f / 10;
    private boolean finished;
    private boolean running;

    @Override
    public void initialize() {
        log.debug("World init");
        initAlgorithms();
        Preferences prefs = Gdx.app.getPreferences(LAST_USED_CONFIG_KEY);
        load(prefs);
    }

    public MainWorldParams getParams() {
        return params;
    }

    public void createGrid() {
        running = false;
        finished = false;

        Preferences prefs = Gdx.app.getPreferences(LAST_USED_CONFIG_KEY);
        save(prefs);
        prefs.flush();

        grid = HexGridBuilder.start()
                .rect(params.width,params.height)
                .hexParams(hexWidth, hexHeight, hexIncline)
                .weight(params.defaultWeight)
                .layers(1)
                .build();
        gridCreated = true;

        fillLists();

        generateCities();
        generateObstacles();

        algorithm.reset();
    }

    private void load(Preferences prefs) {
        params.load(prefs);
        setStepsPerSec(prefs.getInteger("stepsPerSec", getStepsPerSec()));
        setAlgorithm(prefs.getString("algorithm"));
        for (Algorithm algorithm : algorithms) {
            algorithm.load("algorithm", prefs);
        }
    }

    private void initAlgorithms() {
        algorithms.add(StraightforwardAlgorithm.instance(this));
        algorithms.add(AntsAlgorithm.instance(this));
    }

    private void save(Preferences prefs) {
        params.save(prefs);
        prefs.putInteger("stepsPerSec", getStepsPerSec());
        prefs.putString("algorithm", algorithm.toString());
        for (Algorithm algorithm : algorithms) {
            algorithm.save("algorithm", prefs);
        }
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
            hex.setTile(0, HexType.CITY);
            emptyHexes.removeValue(hex, true);
            cities.add(hex);
        }
    }

    private void generateObstacles() {
        int n = params.obstacleLevel * grid.getHexCount() / 16;

        while (n-- > 0) {
            Hex hex = RandomGen.INSTANCE.getRandomItem(emptyHexes);
            hex.setTile(0, HexType.OBSTACLE);
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

    public int getStepsPerSec() {
        return Math.round(1 / updateDelay);
    }

    public void setAlgorithm(String algorithmName) {
        for (Algorithm alg : algorithms) {
            if (algorithmName.equals(alg.toString())) {
                setAlgorithm(alg);
                return;
            }
        }
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        reset();
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setDefaultWeight(float weight) {
        params.setDefaultWeight(weight);
        reset();
    }

    public void setRoadWeight(float weight) {
        params.setRoadWeight(weight);
        reset();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFinished() {
        return finished;
    }

    void reset() {
        if (grid == null)
            return;

        final HexGrid grid = this.grid;
        final float weight = params.defaultWeight;
        for (final HexRow row : grid) {
            for (final Hex hex : row) {
                for (int i = 0; i < 3; ++i) {
                    Edge<Hex> next = grid.next(hex, i);
                    if (next == null)
                        continue;

                    grid.setWeight(hex, next.getNode(), weight);
                }
            }
        }

        algorithm.reset();
    }
}

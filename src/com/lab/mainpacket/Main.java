package com.lab.mainpacket;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.*;

public class Main {

    public static class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Tuple) {
                return this.x.equals(((Tuple) obj).x) && this.y.equals(((Tuple) obj).y);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (x.hashCode() ^ y.hashCode());
        }
    }

    public static class Triplet<X, Y, Z> {
        public final X x;
        public final Y y;
        public final Z z;

        public Triplet(X x, Y y, Z z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof  Triplet) {
                return x.equals(((Triplet) obj).x) && y.equals(((Triplet) obj).y) && z.equals(((Triplet) obj).z);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return x.hashCode() ^ y.hashCode() ^ z.hashCode();
        }
    }

    public static class DataSet {
        public final List<Triplet<Integer, Integer, Float>> trainData;
        public final List<Triplet<Integer, Integer, Float>> testData;
        public final int m;
        public final int n;

        public DataSet(List<Triplet<Integer, Integer, Float>> data, List<Triplet<Integer, Integer, Float>> testData, int m, int n) {
            this.trainData = data;
            this.testData = testData;
            this.m = m;
            this.n = n;
        }
    }

    public static class DataSetClicksViews {
        public final List<Triplet<Integer, Integer, Tuple<Integer, Integer>>> trainData;
        public final List<Triplet<Integer, Integer, Tuple<Integer, Integer>>> testData;
        public final int m;
        public final int n;

        public DataSetClicksViews(List<Triplet<Integer, Integer, Tuple<Integer, Integer>>> data, List<Triplet<Integer, Integer, Tuple<Integer, Integer>>> testData, int m, int n) {
            this.trainData = data;
            this.testData = testData;
            this.m = m;
            this.n = n;
        }
    }

    private static HashMap<String, Integer> appDictionary = new HashMap<String, Integer>();
    private static HashMap<String, Integer> adDictionary = new HashMap<String, Integer>();
    private static int nextApp = 0;
    private static int nextAd = 0;


    private static DataSet getDataSetSQL() throws Exception {
        ArrayList<Triplet<Integer, Integer, Float>> data = new ArrayList<Triplet<Integer, Integer, Float>>();
        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // this will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // setup the connection with the DB.
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/adv?"+ "user=root");

            // statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // resultSet gets the result of the SQL query
            resultSet = statement
                    .executeQuery("SELECT `widget_code`, `link_id`, SUM(`clicks`) as clicks, SUM(`views`) as views FROM `widget_publisher_code` WHERE 1 GROUP BY `widget_code`,`link_id`");

            String appID;
            String advID;
            int views;
            int clicks;
            float rate;

            while (resultSet.next()) {
                appID = resultSet.getString("widget_code");
                advID = resultSet.getString("link_id");
                clicks = resultSet.getInt("clicks");
                views = resultSet.getInt("views");

                rate = clicks/(float)views;
                int app;
                int ad;
                if (appDictionary.containsKey(appID)) {
                    app = appDictionary.get(appID);
                } else {
                    app = nextApp++;
                    appDictionary.put(appID, app);
                }

                if (adDictionary.containsKey(advID)) {
                    ad = adDictionary.get(advID);
                } else {
                    ad = nextAd++;
                    adDictionary.put(advID, ad);
                }
                data.add(new Triplet<Integer, Integer, Float>(ad, app, rate));

            }

            Collections.shuffle(data);
            int index = 4 * data.size() / 5;

            return new DataSet(data.subList(0, index), data.subList(index, data.size()), nextAd, nextApp);
        } catch (Exception e) {
            throw e;
        } finally {
            close(resultSet);
            close(statement);
            close(connect);
        }
    }


    private static DataSetClicksViews getDataSetClicksViewsSQL() throws Exception {
        ArrayList<Triplet<Integer, Integer, Tuple<Integer, Integer>>> data = new ArrayList<Triplet<Integer, Integer, Tuple<Integer, Integer>>>();
        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // this will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // setup the connection with the DB.
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost:3307/amobi?"+ "user=root");

            // statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // resultSet gets the result of the SQL query
            resultSet = statement
                    .executeQuery("SELECT `widget_code`, `link_id`, SUM(`clicks`) as clicks, SUM(`views`) as views FROM `widget_publisher_code` WHERE 1 GROUP BY `widget_code`,`link_id`");

            String appID;
            String advID;
            int views;
            int clicks;
            float rate;

            while (resultSet.next()) {
                appID = resultSet.getString("widget_code");
                advID = resultSet.getString("link_id");
                clicks = resultSet.getInt("clicks");
                views = resultSet.getInt("views");

                rate = clicks/(float)views;
                int app;
                int ad;
                if (appDictionary.containsKey(appID)) {
                    app = appDictionary.get(appID);
                } else {
                    app = nextApp++;
                    appDictionary.put(appID, app);
                }

                if (adDictionary.containsKey(advID)) {
                    ad = adDictionary.get(advID);
                } else {
                    ad = nextAd++;
                    adDictionary.put(advID, ad);
                }
                data.add(new Triplet<Integer, Integer, Tuple<Integer, Integer>>(ad, app, new Tuple<Integer, Integer>(clicks, views)));

            }

            Collections.shuffle(data);
            int index = 4 * data.size() / 5;

            return new DataSetClicksViews(data.subList(0, index), data.subList(index, data.size()), nextAd, nextApp);
        } catch (Exception e) {
            throw e;
        } finally {
            close(resultSet);
            close(statement);
            close(connect);
        }
    }

    private static void close(AutoCloseable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            // don't throw now as it might leave following closables in undefined state
        }
    }

//    private static DataSetClicksViews getDataSetClicksViewsMongo(String dbName, String collectionName) {
//        ArrayList<Triplet<Integer, Integer, Tuple<Integer, Integer>>> data = new ArrayList<Triplet<Integer, Integer, Tuple<Integer, Integer>>>();
//        HashMap<Tuple<Integer, Integer>, Tuple<Integer, Integer>> dataMap = new HashMap<Tuple<Integer, Integer>, Tuple<Integer, Integer>>();
//        try {
//            Mongo client = new Mongo("127.0.0.1", 27017);
//            DB db = client.getDB(dbName);
//            //boolean auth = db.authenticate("admin", "admin".toCharArray());
//
//            DBCollection coll = db.getCollection(collectionName);
//            BasicDBObject query = new BasicDBObject("_id.widget_code", new BasicDBObject("$ne", ""));
//            DBCursor cursor = coll.find(query);
//            try {
//                while(cursor.hasNext()) {
//                    DBObject dbObject = cursor.next();
//                    BasicDBObject id = (BasicDBObject) dbObject.get("_id");
//                    String appId = id.get("widget_code").toString();
//                    String adId = id.get("link_id").toString();
//                    BasicDBObject value = (BasicDBObject)dbObject.get("value");
//                    int clicks = value.getInt("clicks");
//                    int views = value.getInt("views");
//
//                    if (views == 0)
//                        continue;
//
//                    int app;
//                    int ad;
//                    if (appDictionary.containsKey(appId)) {
//                        app = appDictionary.get(appId);
//                    } else {
//                        app = nextApp++;
//                        appDictionary.put(appId, app);
//                    }
//
//                    if (adDictionary.containsKey(adId)) {
//                        ad = adDictionary.get(adId);
//                    } else {
//                        ad = nextAd++;
//                        adDictionary.put(adId, ad);
//                    }
//                    Tuple<Integer, Integer> key = new Tuple<Integer, Integer>(ad, app);
//                    if (dataMap.containsKey(key)) {
//                        Tuple<Integer, Integer> val = dataMap.get(key);
//                        dataMap.put(key, new Tuple<Integer, Integer>(val.x + clicks, val.y + views));
//                    }
//                    else {
//                        dataMap.put(key, new Tuple<Integer, Integer>(clicks, views));
//                    }
//                    //data.add(new Triplet<Integer, Integer, Float>(ad, app, count));
//                }
//                for (Tuple<Integer, Integer> key : dataMap.keySet()) {
//                    Tuple<Integer, Integer> value = dataMap.get(key);
//                    data.add(new Triplet<Integer, Integer, Tuple<Integer, Integer>>(key.x, key.y, value));
//                }
//            } finally {
//                cursor.close();
//            }
//
//            client.close();
//
//            Collections.shuffle(data);
//            int index = 4 * data.size() / 5;
//
//            return new DataSetClicksViews(data.subList(0, index), data.subList(index, data.size()), nextAd, nextApp);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//        throw new IllegalArgumentException();
//
//    }
//
//    private static DataSet getDataSetMongo(String dbName, String collectionName) {
//        ArrayList<Triplet<Integer, Integer, Float>> data = new ArrayList<Triplet<Integer, Integer, Float>>();
//        HashMap<Tuple<Integer, Integer>, Tuple<Integer, Integer>> dataMap = new HashMap<Tuple<Integer, Integer>, Tuple<Integer, Integer>>();
//        try {
//            Mongo client = new Mongo("127.0.0.1", 27017);
//            DB db = client.getDB(dbName);
//            //boolean auth = db.authenticate("admin", "admin".toCharArray());
//
//            DBCollection coll = db.getCollection(collectionName);
//            BasicDBObject query = new BasicDBObject("_id.widget_code", new BasicDBObject("$ne", ""));
//            DBCursor cursor = coll.find(query);
//            try {
//                while(cursor.hasNext()) {
//                    DBObject dbObject = cursor.next();
//                    BasicDBObject id = (BasicDBObject) dbObject.get("_id");
//                    String appId = id.get("widget_code").toString();
//                    String adId = id.get("link_id").toString();
//                    BasicDBObject value = (BasicDBObject)dbObject.get("value");
//                    int clicks = value.getInt("clicks");
//                    int views = value.getInt("views");
//
//                    if (views == 0)
//                        continue;
//
//                    int app;
//                    int ad;
//                    if (appDictionary.containsKey(appId)) {
//                        app = appDictionary.get(appId);
//                    } else {
//                        app = nextApp++;
//                        appDictionary.put(appId, app);
//                    }
//
//                    if (adDictionary.containsKey(adId)) {
//                        ad = adDictionary.get(adId);
//                    } else {
//                        ad = nextAd++;
//                        adDictionary.put(adId, ad);
//                    }
//                    Tuple<Integer, Integer> key = new Tuple<Integer, Integer>(ad, app);
//                    if (dataMap.containsKey(key)) {
//                        Tuple<Integer, Integer> val = dataMap.get(key);
//                        dataMap.put(key, new Tuple<Integer, Integer>(val.x + clicks, val.y + views));
//                    }
//                    else {
//                        dataMap.put(key, new Tuple<Integer, Integer>(clicks, views));
//                    }
//                    //data.add(new Triplet<Integer, Integer, Float>(ad, app, count));
//                }
//                for (Tuple<Integer, Integer> key : dataMap.keySet()) {
//                    Tuple<Integer, Integer> value = dataMap.get(key);
//                    data.add(new Triplet<Integer, Integer, Float>(key.x, key.y, ((float)value.x)/value.y));
//                }
//            } finally {
//                cursor.close();
//            }
//
//            client.close();
//
//            Collections.shuffle(data);
//            int index = 4 * data.size() / 5;
//
//            return new DataSet(data.subList(0, index), data.subList(index, data.size()), nextAd, nextApp);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//        throw new IllegalArgumentException();
//    }

    public static void main(String[] args) {
        try {
            DataSetClicksViews data = getDataSetClicksViewsSQL();
            // DataSetClicksViews data = getDataSetClicksViewsMongo("amobi", "app_ads_views_clicks_days");
            TestNMFClicksViews(data);
            //TestNMFBestRMSE(data, 5000);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void TestNMFClicksViews(DataSetClicksViews data) {
        Tuple<Float[][], Float[][]> result = NMFClicksView(data.trainData, data.m, data.n, 20, 5000, 0.001f, 0.003f);

        float test = 0;
        float percentTest = 0;
        for (Triplet<Integer, Integer, Tuple<Integer, Integer>> d : data.testData) {
            float predict = prediction(result.x, result.y, d.x, d.y);

            //if (predict < 0) predict = 0;
            
            //value = click / views
            float value = ((float)d.z.x)/d.z.y;
            float error = value - predict;
            if (d.z.y >= 500 ) System.out.printf("Predict %f while actually %f -> error = %f, %f%% with views = %d\n", predict, value, error, error * 100/value, d.z.y);
            test += error * error;
            percentTest += Math.abs(error * 100/value);
        }
        test /= data.testData.size();
        percentTest /= data.testData.size();
        System.out.printf("RMSE in test dataset: %f and avr relative error %f%%\n", Math.sqrt(test), percentTest);
    }

    private static void TestNMFWithFeatureScaling(DataSet data) {
        Triplet<Float[][], Float[][], Float[][]> result = NMFWithFeatureScaling(data.trainData, data.m, data.n, 25, 5000, 0.0003f, 0.01f);

        Float[][] scale = result.z;
        List<Triplet<Integer, Integer, Float>> normalizedTestData = new ArrayList<Triplet<Integer, Integer, Float>>();
        for (Triplet<Integer, Integer, Float> d: data.testData) {
            normalizedTestData.add(new Triplet<Integer, Integer, Float>(d.x, d.y, (d.z - scale[d.y][0]) / scale[d.y][1]));
        }

        float test = 0;
        float percentTest = 0;
        for (Triplet<Integer, Integer, Float> d : normalizedTestData) {
            float predict = prediction(result.x, result.y, d.x, d.y);
            float error = d.z - predict;
            System.out.printf("Predict %f while actually %f -> error = %f, %f%%\n", predict, d.z, error, error * 100/d.z);
            test += error * error;
            percentTest += Math.abs(error * 100/d.z);
        }
        test /= data.testData.size();
        percentTest /= data.testData.size();
        System.out.printf("Avr sqr error in test dataset: %f and avr relative error %f%%\n", test, percentTest);
    }

    private static void TestNMFBestRMSE(DataSet data, int maxLoop) {
        float alphas[] = {0.003f, 0.01f, 0.03f, 0.1f};
        float lambdas[] = {0.03f, 0.1f, 0.3f, 1f, 3f};
        int featureNumbers[] = {10, 13, 15, 17, 20, 23, 25, 27, 30};

        for (float alpha : alphas) {
            for (float lambda : lambdas) {
                for (int k : featureNumbers) {
                    float totalRMSE = 0;
                    for (int i = 0; i < 10; ++ i) {
                        Tuple<Float[][], Float[][]> result = NMF(data.trainData, data.m, data.n, k, maxLoop, alpha, lambda);
                        float totalSquareError = 0;

                        for (Triplet<Integer, Integer, Float> d : data.testData) {
                            float predict = prediction(result.x, result.y, d.x, d.y);

                            //if (predict < 0) predict = 0;

                            float error = d.z - predict;
                            //System.out.printf("Predict %f while actually %f -> error = %f, %f%%\n", predict, d.z, error, error * 100/d.z);
                            totalSquareError += error * error;
                        }
                        float MSE = totalSquareError / data.testData.size();
                        totalRMSE += Math.sqrt(MSE);
                    }
                    float RMSE = totalRMSE / 10;

                    System.out.printf("%d features, alpha = %f, lambda = %f  ->  RMSE = %f\n", k, alpha, lambda, RMSE);
                }
            }
        }
    }

    private static String FindKey(HashMap<String, Integer> map, int i) {
        for (String key : map.keySet()) {
            if (i == map.get(key))
                return key;
        }
        return null;
    }

    private static void TestNMFBias(DataSet data) {
        Triplet<Float, Tuple<Float[][], Float[][]>, Tuple<Float[], Float[]>> result = NMFWithBias(data.trainData, data.m, data.n, 20, 5000, 0.0003f, 0.3f);

        float test = 0;
        for (Triplet<Integer, Integer, Float> d : data.testData) {
            float predict = PredictWithBias(result.y, result.z, result.x, d.x, d.y);


            float error = d.z - predict;
            System.out.printf("%s and %s -> %f while %f -> error = %f, %f%%\n", FindKey(appDictionary, d.y), FindKey(adDictionary, d.x), predict, d.z, error, error * 100/d.z);
            test += error * error;
        }
        test /= data.testData.size();
        System.out.printf("RMSE in test dataset: %f \n", Math.sqrt(test));
    }

    private static void TestNMF(DataSet data) {
        Tuple<Float[][], Float[][]> result = NMF(data.trainData, data.m, data.n, 20, 5000, 0.001f, 0.3f);

        float test = 0;
        float percentTest = 0;
        for (Triplet<Integer, Integer, Float> d : data.testData) {
            float predict = prediction(result.x, result.y, d.x, d.y);

            //if (predict < 0) predict = 0;

            float error = d.z - predict;
            System.out.printf("Predict %f while actually %f -> error = %f, %f%%\n", predict, d.z, error, error * 100/d.z);
            test += error * error;
            percentTest += Math.abs(error * 100/d.z);
        }
        test /= data.testData.size();
        percentTest /= data.testData.size();
        System.out.printf("RMSE in test dataset: %f and avr relative error %f%%\n", Math.sqrt(test), percentTest);
    }

    private static float prediction(Float[][] P, Float[][] Q, Integer x, Integer y) {
        return dotProduct(P[x], Q[y]);
    }

    private static Triplet<Float[][], Float[][], Float[][]> NMFWithFeatureScaling(List<Triplet<Integer, Integer, Float>> data,
                                                                                  int m,
                                                                                  int n,
                                                                                  int k,
                                                                                  int maxLoop,
                                                                                  float alpha,
                                                                                  float beta) {

        Float[][] scale = new Float[n][2];
        float[] max = new float[n];
        float[] min = new float[n];
        for (Triplet<Integer, Integer, Float> d : data) {
            if (max[d.y] < d.z)
                max[d.y] = d.z;

            if (min[d.y] > d.z)
                min[d.y] = d.z;
        }

        for (int index = 0; index < n; ++index) {
            scale[index][0] = min[index];
            scale[index][1] = max[index] - min[index];
        }

        List<Triplet<Integer, Integer, Float>> normalized = new ArrayList<Triplet<Integer, Integer, Float>>();
        for (Triplet<Integer, Integer, Float> d: data) {
            normalized.add(new Triplet<Integer, Integer, Float>(d.x, d.y, (d.z - scale[d.y][0])/scale[d.y][1]));
        }

        Tuple<Float[][], Float[][]> pq = NMF(normalized, m, n, k, maxLoop, alpha, beta);
        return new Triplet<Float[][], Float[][], Float[][]>(pq.x, pq.y, scale);
    }


    private static float PredictWithBias(Tuple<Float[][], Float[][]> relation, Tuple<Float[], Float[]> bias, float mean, int i, int j) {
        float predict = dotProduct(relation.x[i], relation.y[j]) + mean + bias.x[i] + bias.y[j];
        return predict;
    }

    private static Triplet<Float, Tuple<Float[][], Float[][]>, Tuple<Float[], Float[]>> NMFWithBias(
            List<Triplet<Integer, Integer, Float>> data,
            int m,
            int n,
            int k,
            int maxLoop,
            float alpha,
            float beta) {

        Random rnd = new Random();

        Float[][] P = new Float[m][k];
        Float[] A = new Float[m];
        for (int i = 0; i < m; i++) {
            A[i] = rnd.nextFloat();
            for (int j = 0; j < k; j++)
                P[i][j] = rnd.nextFloat();
        }
        Float[][] Q = new Float[n][k];
        Float[] B = new Float[n];
        for (int i = 0; i < n; i++) {
            B[i] = rnd.nextFloat();
            for (int j = 0; j < k; j++)
                Q[i][j] = rnd.nextFloat();
        }

        float total  = 0;
        for (Triplet<Integer, Integer, Float> d : data) {
            total += d.z;
        }
        float mean = total / data.size();

        Tuple<Float[][], Float[][]> relation = new Tuple<Float[][], Float[][]>(P, Q);
        Tuple<Float[], Float[]> bias = new Tuple<Float[], Float[]>(A, B);
        double oldCost = 0;
        for (int i = 0; i < maxLoop; ++i) {
            double cost = 0;
            for (Triplet<Integer, Integer, Float> d : data) {
                float error = d.z - PredictWithBias(relation, bias, mean, d.x, d.y);
                cost += error * error;
                for (int j = 0; j < k; j++) {
                    P[d.x][j] += alpha * (error * Q[d.y][j] - beta * P[d.x][j]);
                }

                error = d.z - PredictWithBias(relation, bias, mean, d.x, d.y);
                for (int j = 0; j < k; j++) {
                    Q[d.y][j] += alpha * (error * P[d.x][j] - beta * Q[d.y][j]);
                }

                error = d.z - PredictWithBias(relation, bias, mean, d.x, d.y);
                A[d.x] += alpha*error;

                error = d.z - PredictWithBias(relation, bias, mean, d.x, d.y);
                B[d.y] += alpha*error;
            }
            cost = cost / data.size();
            //System.out.printf("Loop %d with cost %f\n", i, cost);
            if (Math.abs((cost - oldCost)/cost) < 0.0001f || i == (maxLoop - 1)) {
                System.out.printf("After %d loop  ->  cost = %f\n", i, cost);
                break;
            }
            oldCost = cost;
        }

        return new Triplet<Float, Tuple<Float[][], Float[][]>, Tuple<Float[], Float[]>>(mean, relation, bias);
    }

    private static Tuple<Float[][], Float[][]> NMFClicksView(List<Triplet<Integer, Integer, Tuple<Integer, Integer>>> data,
    int m,
    int n,
    int k,
    int maxLoop,
    float alpha,
    float beta)

    {
        Random rnd = new Random();

        Float[][] P = new Float[m][k];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++)
                P[i][j] = rnd.nextFloat();
        }
        Float[][] Q = new Float[n][k];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++)
                Q[i][j] = rnd.nextFloat();
        }

        double oldCost = 0;
        for (int i = 0; i < maxLoop; ++i) {
            double cost = 0;
            for (Triplet<Integer, Integer, Tuple<Integer, Integer>> d : data) {
                //ham xac dinh error: error = X - P*Q
                float error = ((float)d.z.x)/d.z.y - dotProduct(P[d.x], Q[d.y]);
                error *= weightFunction(d.z.y, d.z.x);

                if (Float.isNaN(error) || Float.isInfinite(error)) {
                    Float[] p = P[d.x];
                    Float[] q = Q[d.y];
                    boolean dumb = true;
                }
                cost += error * error;
                for (int j = 0; j < k; j++) {
                    P[d.x][j] += alpha * (error * Q[d.y][j] - beta * P[d.x][j]);
                    if (Float.isNaN(P[d.x][j]) || Float.isInfinite(P[d.x][j])) {
                        boolean dumb = true;
                    }
                }

                error = ((float)d.z.x)/d.z.y - dotProduct(P[d.x], Q[d.y]);
                error *= weightFunction(d.z.y, d.z.x);
                for (int j = 0; j < k; j++) {
                    Q[d.y][j] += alpha * (error * P[d.x][j] - beta * Q[d.y][j]);
                    if (Float.isNaN(Q[d.y][j]) || Float.isInfinite(Q[d.y][j])) {
                        boolean dumb = true;
                    }
                }
            }
            cost = cost / data.size();
            //System.out.printf("Loop %d with cost %f\n", i, cost);
            if (Math.abs((cost - oldCost)/cost) < 0.0001f || i == (maxLoop - 1)) {
                System.out.printf("After %d loop  ->  cost = %f\n", i, cost);
                break;
            }
            oldCost = cost;
        }

        return new Tuple<Float[][], Float[][]>(P, Q);
    }

    private static float weightFunction(int x, int y) {
        float a;
        if (x < 100)
            a =  0.05f;
        else if (x < 500) a = 0.15f;
        else if (x < 1000) a = 0.5f;
        else if (x < 2000) a = 0.7f;
        else a = 1;

        float test = (((float)y)/x);
        if (test < 0.7f)
            return a;
        else return 0.7f /test * a;
    }

    private static Tuple<Float[][], Float[][]> NMF(List<Triplet<Integer, Integer, Float>> data,
                                                                     int m,
                                                                     int n,
                                                                     int k,
                                                                     int maxLoop,
                                                                     float alpha,
                                                                     float beta) {

        Random rnd = new Random();

        Float[][] P = new Float[m][k];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++)
                P[i][j] = rnd.nextFloat();
        }
        Float[][] Q = new Float[n][k];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++)
                Q[i][j] = rnd.nextFloat();
        }

        double oldCost = 0;
        for (int i = 0; i < maxLoop; ++i) {
            double cost = 0;
            for (Triplet<Integer, Integer, Float> d : data) {
                float error = d.z - dotProduct(P[d.x], Q[d.y]);
                if (Float.isNaN(error) || Float.isInfinite(error)) {
                    Float[] p = P[d.x];
                    Float[] q = Q[d.y];
                    boolean dumb = true;
                }
                cost += error * error;
                for (int j = 0; j < k; j++) {
                    P[d.x][j] += alpha * (error * Q[d.y][j] - beta * P[d.x][j]);
                    if (Float.isNaN(P[d.x][j]) || Float.isInfinite(P[d.x][j])) {
                        boolean dumb = true;
                    }
                }

                error = d.z - dotProduct(P[d.x], Q[d.y]);
                for (int j = 0; j < k; j++) {
                    Q[d.y][j] += alpha * (error * P[d.x][j] - beta * Q[d.y][j]);
                    if (Float.isNaN(Q[d.y][j]) || Float.isInfinite(Q[d.y][j])) {
                        boolean dumb = true;
                    }
                }
            }
            cost = cost / data.size();
            //System.out.printf("Loop %d with cost %f\n", i, cost);
            if (Math.abs((cost - oldCost)/cost) < 0.0001f || i == (maxLoop - 1)) {
                System.out.printf("After %d loop  ->  cost = %f\n", i, cost);
                break;
            }
            oldCost = cost;
        }

        return new Tuple<Float[][], Float[][]>(P, Q);
    }

    private static Tuple<Float[][], Float[][]> NMF_D_Lee(List<Triplet<Integer, Integer, Float>> data,
                                                   int m,
                                                   int n,
                                                   int k,
                                                   int maxLoop) {

        Random rnd = new Random();

        Float[][] P = new Float[m][k];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++)
                P[i][j] = rnd.nextFloat();
        }
        Float[][] Q = new Float[n][k];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++)
                Q[i][j] = rnd.nextFloat();
        }

        double oldCost = 0;
        for (int i = 0; i < maxLoop; ++i) {
            double cost = 0;

            for (int k0 = 0; k0 < k; k0++) {
                float[] PW = new float[n];
                float[] PPQ = new float[n];
                float[] WQ = new float[m];
                float[] PQQ = new float[m];
                for (Triplet<Integer, Integer, Float> d : data) {
                    //TODO implement algorithm here
                }

            }
/*
            for (Triplet<Integer, Integer, Float> d : data) {
                float error = d.z - dotProduct(P[d.x], Q[d.y]);
                cost += error * error;
                for (int j = 0; j < k; j++) {
                    P[d.x][j] += alpha * (error * Q[d.y][j] - beta * P[d.x][j]);
                }

                error = d.z - dotProduct(P[d.x], Q[d.y]);
                for (int j = 0; j < k; j++) {
                    Q[d.y][j] += alpha * (error * P[d.x][j] - beta * Q[d.y][j]);
                }
            }
            */
            cost = cost / data.size();
            System.out.printf("Loop %d with cost %f\n", i, cost);
            if (Math.abs((cost - oldCost)/cost) < 0.0001f)
                break;
            oldCost = cost;
        }

        return new Tuple<Float[][], Float[][]>(P, Q);
    }

    private static float dotProduct(Float[] u, Float[] v) {
        if (u.length != v.length)
            throw new IllegalArgumentException("Cannot dot product 2 vector with different size");
        float val = 0;
        for (int i = 0; i < u.length; ++i) {
            val += u[i] * v[i];
        }

        return val;
    }
}

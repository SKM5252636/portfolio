package matrix;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.*;

public class MatrixMultiplication {

    public static class MatrixMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split(",");
            String matrix = parts[0];
            int i = Integer.parseInt(parts[1]);
            int j = Integer.parseInt(parts[2]);
            int val = Integer.parseInt(parts[3]);

            if (matrix.equals("A")) {
                for (int k = 0; k < 2; k++) {
                    context.write(new Text(i + "," + k), new Text("A," + j + "," + val));
                }
            } else if (matrix.equals("B")) {
                for (int k = 0; k < 2; k++) {
                    context.write(new Text(k + "," + j), new Text("B," + i + "," + val));
                }
            }
        }
    }

    public static class MatrixReducer extends Reducer<Text, Text, Text, Text> {

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Map<Integer, Integer> A = new HashMap<>();
            Map<Integer, Integer> B = new HashMap<>();

            for (Text val : values) {
                String[] parts = val.toString().split(",");
                if (parts[0].equals("A")) {
                    A.put(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                } else {
                    B.put(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                }
            }

            int sum = 0;
            for (int k = 0; k < 2; k++) {
                int a = A.getOrDefault(k, 0);
                int b = B.getOrDefault(k, 0);
                sum += a * b;
            }

            context.write(new Text("C(" + key.toString() + ")"), new Text(String.valueOf(sum)));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MatrixMultiplication <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Matrix Multiplication");

        job.setJarByClass(MatrixMultiplication.class);
        job.setMapperClass(MatrixMapper.class);
        job.setReducerClass(MatrixReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

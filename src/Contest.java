import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class Contest {

	private static final int	LIMIT	= 10;

	@SuppressWarnings("serial")
	static class Index extends HashMap<Integer, ArrayList<Integer>> {
		public void put(Integer key, Integer val) {
			if (this.containsKey(key)) {
				this.get(key).add(val);
			} else {
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(val);
				this.put(key, list);
			}
		}
	}

	static class Count implements Comparable<Count> {

		private int		count;
		private Integer	key;

		public Count(Integer key) {
			this.key = key;
			this.count = 1;
		}

		public int compareTo(Count o) {
			return count > o.count ? -1 : 1;
		}
	}

	@SuppressWarnings("serial")
	static class Counter extends HashMap<Integer, Count> {

		public void increment(Integer key) {
			if (this.containsKey(key)) {
				this.get(key).count++;
			} else {
				this.put(key, new Count(key));
			}
		}

		public Collection<Integer> top(int k) {
			PriorityQueue<Count> queue = new PriorityQueue<Count>();
			queue.addAll(this.values());
			ArrayList<Integer> out = new ArrayList<Integer>(k);
			// System.err.println("--------");
			for (int i = 0; i < k && queue.size() > 0; i++) {
				Count count = queue.remove();
				// System.err.println(count.key + ":" + count.count);
				out.add(count.key);
			}
			return out;
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: Contest DATA TEST K");
			System.exit(1);
		}
		BufferedReader data = new BufferedReader(new FileReader(args[0]));
		BufferedReader test = new BufferedReader(new FileReader(args[1]));
		int k = Integer.valueOf(args[2]);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

		Index userRepos = new Index();
		Index repoUsers = new Index();
		TreeSet<Integer> tmpAllRepos = new TreeSet<Integer>();

		String line;
		while ((line = data.readLine()) != null) {
			String[] arr = line.split(":");
			Integer user = Integer.valueOf(arr[0]);
			Integer repo = Integer.valueOf(arr[1]);
			tmpAllRepos.add(repo);
			userRepos.put(user, repo);
			repoUsers.put(repo, user);
		}

		Integer[] allRepos = tmpAllRepos.toArray(new Integer[0]);

		data.close();

		while ((line = test.readLine()) != null) {
			Integer user = Integer.valueOf(line);
			writer.write(user + ":");
			Counter relatedUserCounts = new Counter();

			ArrayList<Integer> repos = userRepos.get(user);
			if (repos == null) {
				System.err.println("User " + user + " has no repos?!");
			} else {
				for (Integer repo : repos) {
					for (Integer relatedUser : repoUsers.get(repo)) {
						relatedUserCounts.increment(relatedUser);
					}
				}
			}

			Collection<Integer> topUsers = relatedUserCounts.top(k);
			Counter relatedRepoCounts = new Counter();
			for (Integer topUser : topUsers) {
				for (Integer repo : userRepos.get(topUser)) {
					relatedRepoCounts.increment(repo);
				}
			}

			while (relatedRepoCounts.size() < LIMIT) {
				relatedRepoCounts.increment(randomRepo(allRepos));
			}

			Integer[] topRepos = relatedRepoCounts.top(LIMIT).toArray(new Integer[0]);
			for (int i = 0; i < topRepos.length; i++) {
				if (i > 0) writer.write(",");
				writer.write(topRepos[i].toString());
			}
			writer.write("\n");
		}
		writer.close();
		test.close();
	}

	private static Integer randomRepo(Integer[] allRepos) {
		return allRepos[(int) (Math.random() * allRepos.length)];
	}
}

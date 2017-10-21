package org.hotwheel.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A case-insensitive <code>Map</code>.
 * <p>
 * Before keys are added to the map or compared to other existing keys, they are converted
 * to all lowercase in a locale-independent fashion by using information from the Unicode
 * data file.
 * <p>
 * Null keys are supported.
 * <p>
 * The <code>keySet()</code> method returns all lowercase keys, or nulls.
 * <p>
 * Example:
 * <pre><code>
 *  Map&lt;String, String&gt; map = new CaseInsensitiveMap&lt;String, String&gt;();
 *  map.put("One", "One");
 *  map.put("Two", "Two");
 *  map.put(null, "Three");
 *  map.put("one", "Four");
 * </code></pre>
 * creates a <code>CaseInsensitiveMap</code> with three entries.<br>
 * <code>map.get(null)</code> returns <code>"Three"</code> and <code>map.get("ONE")</code>
 * returns <code>"Four".</code>  The <code>Set</code> returned by <code>keySet()</code>
 * equals <code>{"one", "two", null}.</code>
 * <p>
 * <strong>Note that CaseInsensitiveMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedMap(Map)}. This class may throw
 * exceptions when accessed by concurrent threads without synchronization.
 * </p>
 *
 * @since 3.0
 */
public class CaseInsensitiveMap<K, V> extends ConcurrentHashMap<K, V> {

    /**
     * convert keys to lower case.
     * <p>
     * Returns if key is null.
     *
     * @param key  the key convert
     * @return the converted key
     */
    private Object convertKey(final Object key) {
        if (key != null) {
            final char[] chars = key.toString().toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                chars[i] = Character.toLowerCase(Character.toUpperCase(chars[i]));
            }
            return new String(chars);
        }
        return null;
    }

    @Override
    public V get(Object key) {
        Object k = convertKey(key);
        return super.get(k);
    }

    @Override
    public boolean containsKey(Object key) {
        Object k = convertKey(key);
        return super.containsKey(k);
    }

    @Override
    public V put(K key, V value) {
        if (key instanceof Object) {
            Object k = convertKey(key);
            return super.put((K)k, value);
        } else {
            // 这段代码没用, 因为泛型的K和V必须是Object
            return super.put(key, value);
        }
    }

    public static void main(String[] args) {
        Map<Integer, Integer> t1 = new CaseInsensitiveMap<>();
        t1.put(1, 101);
        t1.put(2, 102);
        System.out.println(t1.size());
        System.out.println(t1.get(1));
        System.out.println(t1.get(2));

        Map<String, Integer> t2 = new CaseInsensitiveMap<>();
        t2.put("Abc", 101);
        t2.put("abC", 101);
        t2.put("Abc-aA", 102);
        System.out.println(t2.size());
        System.out.println(t2.get("aBc"));
        System.out.println(t2.get("abc-AA"));
    }
}
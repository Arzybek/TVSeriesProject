package com.tvseries.TvSeries.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtils {
    public static <TvShow> List<List<TvShow>> partition(Collection<TvShow> members, int maxSize)
    {
        List<List<TvShow>> res = new ArrayList<>();

        List<TvShow> internal = new ArrayList<>();

        for (TvShow member : members)
        {
            internal.add(member);

            if (internal.size() == maxSize)
            {
                res.add(internal);
                internal = new ArrayList<>();
            }
        }
        if (!internal.isEmpty())
        {
            res.add(internal);
        }
        return res;
    }
}

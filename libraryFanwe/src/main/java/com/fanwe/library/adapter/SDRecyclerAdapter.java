package com.fanwe.library.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.library.adapter.viewholder.SDRecyclerViewHolder;
import com.fanwe.library.common.SDSelectManager;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView适配器
 *
 * @param <T> 实体类型
 */
public abstract class SDRecyclerAdapter<T> extends RecyclerView.Adapter<SDRecyclerViewHolder<T>> implements ISDAdapter<T>
{

    private Activity activity;
    private List<T> listModel = new ArrayList<>();

    private SDSelectManager<T> selectManager = new SDSelectManager<>();
    private SDSelectManager.SelecteStateListener<T> selectStateListener;

    public void setSelectStateListener(SDSelectManager.SelecteStateListener<T> selectStateListener)
    {
        this.selectStateListener = selectStateListener;
    }

    public SDSelectManager<T> getSelectManager()
    {
        return selectManager;
    }

    public SDRecyclerAdapter(Activity activity)
    {
        this(null, activity);
    }

    public SDRecyclerAdapter(List<T> listModel, Activity activity)
    {
        super();
        this.activity = activity;
        initSDSelectManager();
        setData(listModel);
    }

    @Override
    public Activity getActivity()
    {
        return activity;
    }

    @Override
    public View inflate(int resource, ViewGroup root)
    {
        return inflate(resource, root, false);
    }

    @Override
    public View inflate(int resource, ViewGroup root, boolean attachToRoot)
    {
        return getActivity().getLayoutInflater().inflate(resource, root, attachToRoot);
    }

    @Override
    public int indexOf(T model)
    {
        return listModel.indexOf(model);
    }

    @Override
    public void setData(List<T> list)
    {
        if (list != null)
        {
            this.listModel = list;
        } else
        {
            this.listModel.clear();
        }
        selectManager.setItems(listModel);
        selectManager.synchronizedSelected();
    }

    @Override
    public void updateData(List<T> list)
    {
        setData(list);
        notifyDataSetChanged();
    }

    @Override
    public void clearData()
    {
        updateData(null);
    }

    @Override
    public List<T> getData()
    {
        return listModel;
    }

    @Override
    public T getItemData(int position)
    {
        if (isPositionLegal(position))
        {
            return listModel.get(position);
        }
        return null;
    }

    @Override
    public void appendData(T model)
    {
        if (model != null)
        {
            listModel.add(model);
            selectManager.synchronizedSelected(model);
            notifyItemInserted(listModel.size() - 1);
        }
    }

    @Override
    public void appendData(List<T> list)
    {
        if (list != null && !list.isEmpty())
        {
            int positionStart = listModel.size();
            int itemCount = list.size();

            listModel.addAll(list);
            selectManager.synchronizedSelected(list);
            notifyItemRangeInserted(positionStart, itemCount);
        }
    }

    @Override
    public void removeData(T model)
    {
        if (model != null)
        {
            int position = listModel.indexOf(model);
            removeData(position);
        }
    }

    @Override
    public T removeData(int position)
    {
        T model = null;
        if (isPositionLegal(position))
        {
            selectManager.setSelected(position, false);
            model = listModel.remove(position);
            notifyItemRemoved(position);
        }
        return model;
    }

    @Override
    public void insertData(int position, T model)
    {
        if (model != null)
        {
            listModel.add(position, model);
            selectManager.synchronizedSelected(position);
            notifyItemInserted(position);
        }
    }

    @Override
    public void insertData(int position, List<T> list)
    {
        if (list != null && !list.isEmpty())
        {
            int positionStart = position;
            int itemCount = list.size();

            listModel.addAll(position, list);
            selectManager.synchronizedSelected(list);
            notifyItemRangeInserted(positionStart, itemCount);
        }
    }

    @Override
    public void updateData(int position, T model)
    {
        if (model != null && isPositionLegal(position))
        {
            listModel.set(position, model);
            selectManager.synchronizedSelected(model);
            notifyItemChanged(position);
        }
    }

    @Override
    public void updateData(int position)
    {
        if (isPositionLegal(position))
        {
            notifyItemChanged(position);
        }
    }

    @Override
    public boolean isPositionLegal(int position)
    {
        if (position >= 0 && position < listModel.size())
        {
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount()
    {
        if (listModel != null)
        {
            return listModel.size();
        }
        return 0;
    }

    @Override
    public final void onBindViewHolder(SDRecyclerViewHolder<T> holder, int position, List<Object> payloads)
    {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public final void onBindViewHolder(SDRecyclerViewHolder<T> holder, int position)
    {
        T model = getItemData(position);

        holder.onBindData(position, model);
        onBindData(holder, position, model);
    }

    @Override
    public final SDRecyclerViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        SDRecyclerViewHolder<T> holder = onCreateVHolder(parent, viewType);
        holder.setAdapter(this);

        return holder;
    }

    public abstract SDRecyclerViewHolder<T> onCreateVHolder(ViewGroup parent, int viewType);

    /**
     * 重写此方法绑定数据
     *
     * @param holder
     * @param position
     * @param model
     */
    public abstract void onBindData(SDRecyclerViewHolder<T> holder, int position, T model);

    private void initSDSelectManager()
    {
        selectManager.setMode(SDSelectManager.Mode.SINGLE);
        selectManager.setListener(new SDSelectManager.SDSelectManagerListener<T>()
        {
            @Override
            public void onNormal(int index, T item)
            {
                onNormalItem(index, item);
                if (selectStateListener != null)
                {
                    selectStateListener.onNormal(index, item);
                }
            }

            @Override
            public void onSelected(int index, T item)
            {
                onSelectedItem(index, item);
                if (selectStateListener != null)
                {
                    selectStateListener.onSelected(index, item);
                }
            }
        });
    }

    /**
     * item被置为正常状态的时候回调
     *
     * @param position
     * @param item
     */
    protected void onNormalItem(int position, T item)
    {
        if (item instanceof SDSelectManager.SDSelectable)
        {
            SDSelectManager.SDSelectable selectable = (SDSelectManager.SDSelectable) item;
            selectable.setSelected(false);
        }
        updateData(position);
    }

    /**
     * item被置为选中状态的时候回调
     *
     * @param position
     * @param item
     */
    protected void onSelectedItem(int position, T item)
    {
        if (item instanceof SDSelectManager.SDSelectable)
        {
            SDSelectManager.SDSelectable selectable = (SDSelectManager.SDSelectable) item;
            selectable.setSelected(true);
        }
        updateData(position);
    }

}

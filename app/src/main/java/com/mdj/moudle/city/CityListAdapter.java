package com.mdj.moudle.city;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.zhy.flowlayout.FlowLayout;
import com.zhy.flowlayout.TagAdapter;
import com.zhy.flowlayout.TagFlowLayout;
import com.zhy.flowlayout.TagFlowLayout.OnTagClickListener;

import java.util.List;

public class CityListAdapter extends BaseAdapter implements
		com.mdj.moudle.city.PinnedHeaderListView.PinnedHeaderAdapter,
		OnScrollListener, OnTagClickListener {
	private List<CityBean> dataList;
	private MySectionIndexer mIndexer;
	private Context context;
	private int mLocationPosition = -1;
	private LayoutInflater mInflater;
	private TagFlowLayout tflHotCity;
	private TextView tvCurrentCityName;
	private List<String> hotCityArray;

	final int TYPE_UPPER = 0;
	final int TYPE_LOWER = 1;

	public CityListAdapter(List<String> hotCityArray, List<CityBean> dataList,
			MySectionIndexer mIndexer, Context context) {
		this.dataList = dataList;
		this.mIndexer = mIndexer;
		this.context = context;
		this.hotCityArray = hotCityArray;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList == null ? 0 : dataList.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position == 0 ? null : dataList.get(position + 1);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		int p = position;
		if (p == 0)
			return TYPE_UPPER;
		else
			return TYPE_LOWER;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		switch (type) {
		case TYPE_UPPER:
			if (position == 0) {
				View view;
				ViewHolder holder = new ViewHolder();
				view = mInflater.inflate(R.layout.city_list_header, null);

				// 当前城市
				tvCurrentCityName = (TextView) view
						.findViewById(R.id.tvCurrentCityName);
				tvCurrentCityName.setText(CacheManager.getInstance().getGlobalCity().getName());

				// 热门城市
				tflHotCity = (TagFlowLayout) view.findViewById(R.id.tflHotCity);
				tflHotCity.setMaxSelectCount(1);
				tflHotCity.setOnTagClickListener(this);
				tflHotCity.setAdapter(new TagAdapter<String>(hotCityArray) {
					@Override
					public View getView(FlowLayout parent, int position,
							String s) {
						TextView tv = (TextView) mInflater.inflate(
								R.layout.tv_for_hot_city, tflHotCity, false);
						tv.setText(s);
                        if(s.equals(CacheManager.getInstance().getGlobalCity().getName())){
                            tv.setBackgroundResource(R.drawable.gray_round_bg);
                        }
						return tv;
					}
				});

				 view.setTag(holder);
				return view;
			}
			break;

		case TYPE_LOWER:
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.select_city_item, null);

				holder = new ViewHolder();
				holder.group_title = (TextView) view
						.findViewById(R.id.group_title);
				holder.city_name = (TextView) view.findViewById(R.id.city_name);
				holder.rlItem = (RelativeLayout) view.findViewById(R.id.rlItem);
				holder.btnScope = (Button) view.findViewById(R.id.btnScope);
                holder.vLine = (View)view.findViewById(R.id.vLine);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			CityBean city = dataList.get(position - 1);

			holder.group_title.setVisibility(View.VISIBLE);
			holder.city_name.setVisibility(View.VISIBLE);

			int section = mIndexer.getSectionForPosition(position);
			if (mIndexer.getPositionForSection(section) == position) {
				holder.group_title.setVisibility(View.VISIBLE);
				holder.group_title.setText(city.getSortKey());
			} else {
				holder.group_title.setVisibility(View.GONE);
			}
            //如果是最后一条 隐藏每一个section的最后一个item的分割线
            if(mIndexer.isLastPositionForSection(section,position)){  //最后一条
                holder.vLine.setVisibility(View.GONE);
                holder.rlItem.setBackgroundResource(R.drawable.white_down_round_bg);
            }else{
                holder.vLine.setVisibility(View.VISIBLE);
                if(mIndexer.getPositionForSection(section) == position){ //第一条
                    holder.rlItem.setBackgroundResource(R.drawable.white_up_round_bg);
                }else{
                    holder.rlItem.setBackgroundResource(R.color.white);
                }
            }
			holder.city_name.setText(city.getName());

			holder.rlItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((OnItemClickListener) context).onItemClick(null, null,
							position, 0);
				}
			});

			holder.btnScope.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent it = new Intent(context, CityMapActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("cityBean", dataList.get(position-1));
					it.putExtras(b);
					context.startActivity(it);
				}
			});
			return view;

		default:
			return null;
		}
		return parent;
	}

	public static class ViewHolder {
		private TextView group_title;
		private TextView city_name;
		private RelativeLayout rlItem;
		private Button btnScope;
        private View vLine;
	}

	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 0
				|| (mLocationPosition != -1 && mLocationPosition == realPosition)) {
			return PINNED_HEADER_GONE;
		}
		mLocationPosition = -1;
		int section = mIndexer.getSectionForPosition(realPosition);
		int nextSectionPosition = mIndexer.getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		// TODO Auto-generated method stub
		int realPosition = position;
		int section = mIndexer.getSectionForPosition(realPosition);
		String title = (String) mIndexer.getSections()[section];
		if (position == 0) {
			((TextView) header.findViewById(R.id.group_title))
					.setBackgroundDrawable(null);
			((TextView) header.findViewById(R.id.group_title)).setText("");
		} else {
			((TextView) header.findViewById(R.id.group_title))
					.setBackgroundColor(context.getResources().getColor(
							R.color.gray));
			((TextView) header.findViewById(R.id.group_title)).setText(title);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public boolean onTagClick(View view, int position, FlowLayout parent) {
		// TODO Auto-generated method stub
		return ((OnTagClickListener) context)
				.onTagClick(view, position, parent);
	}
}

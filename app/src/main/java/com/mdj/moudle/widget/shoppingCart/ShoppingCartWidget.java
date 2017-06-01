package com.mdj.moudle.widget.shoppingCart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.utils.AlertDialogUtils;
import com.mdj.moudle.widget.AddAndSubWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.ToastUtils;
import com.mdj.view.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tt on 2016/6/27.
 */
public class ShoppingCartWidget extends LinearLayout implements View.OnClickListener{
    public static final String ADD_AND_SUB_USER_SHOPPING_CART_WIDGET = "shoppingCartWidget";

    public interface ShoppingCartWidgetSubmitListener{
        void onSubmit(List<GoodsWrapperBean> dataList);
    }
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private RelativeLayout main;

    private RelativeLayout rlShoppingCart;
    private RelativeLayout rlShoppingCartBody;

    private List<GoodsWrapperBean> goodsList;
    private ShoppingCartGoodsListAdapter adapter;
    private MyListView lvProjectList;

    private TextView tvServiceType,tvPrice,tvProjectNum,tvServiceTimeLength,tvHint;
    private RelativeLayout rlHint,rlSubmit;
    private LinearLayout llClean;

    private IntentFilter filter;
    private ShoppingCartWidgetSubmitListener shoppingCartWidgetSubmitListener;
/******************************************************************************************************/
    public ShoppingCartWidget(Context context){
        this(context,null);
    }
    public ShoppingCartWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShoppingCartWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (RelativeLayout)inflater.inflate(R.layout.shopping_cart_widget, null);
        layoutParams = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);

        tvServiceType = (TextView)findViewById(R.id.tvServiceType);
        tvPrice = (TextView)findViewById(R.id.tvPrice);
        tvPrice.setOnClickListener(this);

        tvProjectNum = (TextView)findViewById(R.id.tvProjectNum);
        tvServiceTimeLength = (TextView)findViewById(R.id.tvServiceTimeLength);

        rlHint = (RelativeLayout)findViewById(R.id.rlHint);
        rlSubmit = (RelativeLayout)findViewById(R.id.rlSubmit);
        rlSubmit.setOnClickListener(this);
        tvHint = (TextView)findViewById(R.id.tvHint);

        rlShoppingCartBody = (RelativeLayout)findViewById(R.id.rlShoppingCartBody);
        rlShoppingCart = (RelativeLayout)findViewById(R.id.rlShoppingCart);
        rlShoppingCart.setOnClickListener(this);

        llClean = (LinearLayout)findViewById(R.id.llClean);
        llClean.setOnClickListener(this);

        lvProjectList = (MyListView)findViewById(R.id.lvProjectList);
        goodsList = new ArrayList<>();
        adapter = new ShoppingCartGoodsListAdapter(goodsList);
        lvProjectList.setAdapter(adapter);

        filter = new IntentFilter();
        filter.addAction(AddAndSubWidget.BROADCAST_ACTION_OPERATION+ADD_AND_SUB_USER_SHOPPING_CART_WIDGET);
        context.registerReceiver(receiver, filter);
    }
    public void unregisterReceiver() {
        try{
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void registerReceiver() {
        try{
            context.registerReceiver(receiver, filter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String dataTag = intent.getExtras().getString(AddAndSubWidget.DATA_TAG);
                String action = intent.getExtras().getString("action");
                Map<String,String> tagMap = CommonUtil.transStringToMap(dataTag);

                int position = Integer.valueOf(tagMap.get("position"));
                int num = goodsList.get(position).getNum();
                if(action.equals(AddAndSubWidget.ButtonAction.ActionAdd.toString())){
                    goodsList.get(position).setNum(++num);
                }else{
                    if(num>1){
                        goodsList.get(position).setNum(--num);
                    }else if(num==1){
                        goodsList.remove(position);
                        if(isGoodListEmpty()){
                            cleanShoppingCart();
                            return;
                        }
                    }
                }
                updateShoppingCartUI();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(context, "exception:" + e.toString());
            }
        }
    };

    public void setShoppingCartWidgetSubmitListener(ShoppingCartWidgetSubmitListener shoppingCartWidgetSubmitListener){
        this.shoppingCartWidgetSubmitListener = shoppingCartWidgetSubmitListener;
    }
    public void setServiceTypeText(String text){
        tvServiceType.setText(text);
    }
//增加一个商品
    public void addGoods(GoodsBean bean){
        boolean find = false;
        for(int i=0;i<goodsList.size();i++){
            if(goodsList.get(i).getGoodsBean().getId().equals(bean.getId())){
                find = true;
                goodsList.get(i).setNum(1+goodsList.get(i).getNum());
                break;
            }
        }
        if(!find){
            goodsList.add(new GoodsWrapperBean(bean,1));
        }
        updateShoppingCartUI();
    }

//增加商品list
    public void replaceAllGoods(List<GoodsWrapperBean> goodsList){
        if(goodsList==null)
            return;
        this.goodsList.clear();
        this.goodsList.addAll(goodsList);
        updateShoppingCartUI();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPrice:
            case R.id.rlShoppingCart:
                if(goodsList.size()==0){
                    break;
                }
                if(rlShoppingCartBody.getVisibility()==View.VISIBLE){
                    rlShoppingCartBody.setVisibility(View.GONE);
                    //show the hint
                    rlHint.setVisibility(View.VISIBLE);
                }else{//打开购物车页面
                    rlShoppingCartBody.setVisibility(View.VISIBLE);
                    adapter.setDataList(goodsList);
                    adapter.notifyDataSetChanged();
                    //dismiss the hint
                    rlHint.setVisibility(View.GONE);
                }
                break;

            case R.id.rlSubmit:
                if(shoppingCartWidgetSubmitListener!=null){
                    shoppingCartWidgetSubmitListener.onSubmit(goodsList);
                }
                break;

            case R.id.llClean:
                AlertDialogUtils.onPopupButtonClick(context, "确认要清空购物车吗？", new AlertDialogUtils.doSomethingListener() {
                    @Override
                    public void doSomething() {
                        cleanShoppingCart();
                    }
                });

                break;

            default:
                break;
        }
    }

    private void updateShoppingCartUI(){
        int totalPrice=0,totalProjectNum=0,totalDuration=0;
        for(GoodsWrapperBean bean:goodsList){
            totalPrice+=bean.getGoodsBean().getPrice()*bean.getNum();
            totalProjectNum+=bean.getNum();
            totalDuration+=bean.getGoodsBean().getDuration()*bean.getNum();
        }
        tvPrice.setText(totalPrice + "");
        tvProjectNum.setText(totalProjectNum+"个项目");
        tvServiceTimeLength.setText(totalDuration + "分钟");

        //button
        if(totalProjectNum>0){
            tvHint.setText(totalProjectNum+"");
            if(rlShoppingCartBody.getVisibility()!=VISIBLE){
                rlHint.setVisibility(VISIBLE);
            }
            rlSubmit.setBackgroundResource(R.drawable.red_deep_red_selector);
        }else{
            rlHint.setVisibility(GONE);
            rlSubmit.setBackgroundResource(R.color.mdj_gray);
        }
        adapter.setDataList(goodsList);
        adapter.notifyDataSetChanged();
    }

    public class ShoppingCartGoodsListAdapter extends BaseAdapter {
        private List<GoodsWrapperBean> list;
        public ShoppingCartGoodsListAdapter(List<GoodsWrapperBean> list) {
            this.list = list;
        }

        public void setDataList(List<GoodsWrapperBean> list){
            this.list = list;
        }
        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.shopping_cart_goods_list_item, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);

                holder.addAndSubWidget = (AddAndSubWidget) convertView.findViewById(R.id.addAndSubWidget);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                GoodsWrapperBean bean = list.get(position);
                holder.tvName.setText(bean.getGoodsBean().getName());
                holder.tvPrice.setText(context.getResources().getString(R.string.symbol_rmb) + bean.getGoodsBean().getPrice() + "");
                holder.addAndSubWidget.setValue(bean.getNum());
                holder.addAndSubWidget.setCustomCalMode(false);

                Map<String,String> tagMap = new HashMap<>();
                tagMap.put("position", position + "");

                holder.addAndSubWidget.setDataTagAndWidgetUser(CommonUtil.transMapToString(tagMap), ADD_AND_SUB_USER_SHOPPING_CART_WIDGET);//私有协议

            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            TextView tvName,tvPrice;
            AddAndSubWidget addAndSubWidget;
        }
    }

    private boolean isGoodListEmpty() {
        boolean isEmpty = true;
        for(GoodsWrapperBean bean:goodsList){
            if(bean.getNum()>0)
                isEmpty = false;
        }
        return isEmpty;
    }
    public void cleanShoppingCart(){
        goodsList.clear();
        rlShoppingCartBody.setVisibility(View.GONE);
        updateShoppingCartUI();
    }
    public List<GoodsWrapperBean> getCurrentGoods(){
        /*筛选一下*/
        int i=0;
        while(i!=goodsList.size()){
            if(goodsList.get(i).getNum()==0){
                goodsList.remove(i);
            }else{
                i++;
            }
        }
        /**/
        return goodsList;
    }
}

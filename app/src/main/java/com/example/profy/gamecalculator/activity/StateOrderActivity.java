package com.example.profy.gamecalculator.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;
import com.example.profy.gamecalculator.util.IdentificationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StateOrderActivity extends BaseActivity {
    private OrderListAdapter orderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = findViewById(R.id.state_order_list);
        orderListAdapter = new OrderListAdapter(this);
        listView.setAdapter(orderListAdapter);
        listView.setOnItemClickListener((parent, itemClicked, position, id) -> {
            resolveTransaction(((KryoConfig.StateOrderDto) orderListAdapter.getItem(position)).id);
        });

        receiver.addHandler(NetworkService.STATE_ORDERS_ACTION, Obj -> {
            orderListAdapter.setData(((KryoConfig.StateOrderListDto) Obj).stateOrderList);
        });

        receiver.addHandler(NetworkService.VEXEL_LIST_ACTION, Obj -> {
            StringBuilder stringBuilder = new StringBuilder();
            List<Integer> vexels = ((KryoConfig.VexelListDto) Obj).vexelIdList;
            for (Integer vexelId : vexels) {
                stringBuilder
                        .append(vexelId)
                        .append("\n");
            }
            showInformationDialog("ID Векселей", stringBuilder.toString());
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_state_order;
    }

    @Override
    protected void retrieveEntities() {
        networkService.sendData(new KryoConfig.RequestStateOrderListDto(), this);
    }

    private void resolveTransaction(int orderId) {
        IdentificationAdapter handler = cardId -> {
            nfcHandler = null;
            sendData(cardId, orderId);
        };
        nfcHandler = handler;
        showTransactionDialog("Подтверждение заказа", handler);
    }

    private void sendData(KryoConfig.Identifier identifier, int orderId) {
        Log.d("State order", "Sending data...");
        KryoConfig.ResolveStateOrder stateOrder = new KryoConfig.ResolveStateOrder();
        stateOrder.id = identifier;
        stateOrder.orderId = orderId;
        networkService.sendData(stateOrder, this);
    }

    static class OrderListAdapter extends BaseAdapter {
        private List<KryoConfig.StateOrderDto> data;
        private Context context;

        public OrderListAdapter(Context context) {
            this.context = context;
            data = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void setData(List<KryoConfig.StateOrderDto> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.order_text_view, null, false);
            }

            KryoConfig.StateOrderDto orderDto = (KryoConfig.StateOrderDto) getItem(i);


            ((TextView) view.findViewById(R.id.textView5)).setText(orderDto.productData.name);
            ((TextView) view.findViewById(R.id.textView7)).setText(
                    orderDto.productData.amount
                            + " шт за "
                            + orderDto.moneyAmount
                            + " тенге"
                            + (orderDto.payByVexel ? " векселями" : "")
            );

            return view;
        }
    }
}

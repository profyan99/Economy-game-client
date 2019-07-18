package com.example.profy.gamecalculator.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.profy.gamecalculator.R;
import com.example.profy.gamecalculator.network.KryoConfig;
import com.example.profy.gamecalculator.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

public class StateOrderActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private OrderListAdapter orderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.state_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        orderListAdapter = new OrderListAdapter();
        recyclerView.setAdapter(orderListAdapter);

        receiver.addHandler(NetworkService.RESOURCE_LIST_ACTION, Obj -> {
            orderListAdapter.setData(((KryoConfig.StateOrderListDto) Obj).stateOrderList);
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

    static class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
        private List<KryoConfig.StateOrderDto> data;

        public static class OrderViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public OrderViewHolder(TextView v) {
                super(v);
                textView = v;
            }
        }

        public OrderListAdapter() {
            data = new ArrayList<>();
        }

        public void setData(List<KryoConfig.StateOrderDto> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public OrderListAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_text_view, parent, false);
            return new OrderViewHolder(v);
        }


        @Override
        public void onBindViewHolder(OrderViewHolder holder, int position) {
            StringBuilder builder = new StringBuilder();
            KryoConfig.StateOrderDto model = data.get(position);
            builder
                    .append(model.productData.name)
                    .append("\t")
                    .append(model.productData.amount)
                    .append("\t")
                    .append(model.payByVexel ? "Вексель" : "Валюта")
                    .append("\n")
                    .append(model.moneyAmount);
            holder.textView.setText(builder.toString());

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}

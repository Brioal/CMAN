package com.brioal.cman.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.brioal.baselib.util.ToastUtils;
import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.Ui.DeviceDetailActivity;
import com.brioal.cman.Ui.viewholder.AbstractViewHolder;
import com.brioal.cman.data.DataLoader;
import com.brioal.cman.entity.DeviceEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Device List RecyclerView Adapter RecyclerViewAdapter
 * Created by Brioal on 2016/9/11.
 */
public class DeviceListAdapter extends RecyclerView.Adapter {
    private final int NODATA = 0;
    private final int DEVICE = 1;

    private Context mContext;
    private List<DeviceEntity> mDevices;
    private AlertDialog.Builder mBuilder;


    public void showDeleteListener(final int position) {
        if (mBuilder != null) {
            mBuilder = new AlertDialog.Builder(mContext);
        }
        mBuilder.setTitle("注意").setMessage("是否删除" + mDevices.get(position).getTitle() + "设备？").setNegativeButton("不删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DataLoader.newInstance(mContext).delDeviceInfo(mDevices.get(position).getSSID());
                mDevices.remove(position);
                notifyItemRemoved(position);
            }
        }).setCancelable(true).show();
    }

    public DeviceListAdapter(Context context, List<DeviceEntity> devices) {
        mContext = context;
        mDevices = devices;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NODATA) { //无数据
            return new NoDeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_no_device, parent, false));
        } else if (viewType == DEVICE) {
            return new DeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DeviceViewHolder) {
            final DeviceEntity entity = mDevices.get(position);
            ((DeviceViewHolder) holder).bindView(entity, position);
        }
    }

    @Override
    public int getItemCount() {
        if (mDevices.size() == 0) {
            return 1;
        } else {
            return mDevices.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mDevices.size() == 0) {
            return NODATA;
        } else {
            return DEVICE;
        }
    }

    //无数据ViewHolder
    class NoDeviceViewHolder extends RecyclerView.ViewHolder {

        public NoDeviceViewHolder(View itemView) {
            super(itemView);
        }
    }

    //DeviceViewHolder
    class DeviceViewHolder extends AbstractViewHolder {
        @Bind(R.id.device_iv_head)
        ImageView mIvHead;
        @Bind(R.id.device_tv_title)
        TextView mTvTitle;
        @Bind(R.id.device_tv_state)
        TextView mTvState;
        @Bind(R.id.device_ib_change)
        ImageButton mBtnChange;
        private DeviceEntity mCurrentEntity = null;


        View itemView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }

        //错误提示
        public void showNoticeDialog(String title, String message) {
            mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setMessage(title).setMessage(message).setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }

        private int mCurrentPosition = 0;
        private AlertDialog.Builder mEditBuilder = null;

        //显示修改Dialog
        public void showEditDialog() {
            mEditBuilder = new AlertDialog.Builder(mContext);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit, null, false);
            mEditBuilder.setView(layout);
            final EditText etTitle = (EditText) layout.findViewById(R.id.dialog_edit_et_title);
            TextInputLayout textLayout = (TextInputLayout) layout.findViewById(R.id.dialog_edit_tl_title);
            textLayout.setHint("原名称:" + mDevices.get(mCurrentPosition).getTitle());
            mEditBuilder.setMessage("修改设备名称").setNegativeButton("不修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!etTitle.getText().toString().isEmpty()) {
                        changeDeviceTitle(etTitle.getText().toString());
                        ToastUtils.showToast(mContext, "修改名称成功");
                    } else {
                        etTitle.setError("设备名称不能为空");
                    }
                }
            });
            mEditBuilder.create().show();

        }

        //改变显示名称并更改本地记录
        public void changeDeviceTitle(String newTitle) {
            mDevices.get(mCurrentPosition).setTitle(newTitle);
            notifyItemChanged(mCurrentPosition);
            DataLoader.newInstance(mContext).changeDeviceTitle(mDevices.get(mCurrentPosition).getSSID(), newTitle);
        }

        //显示选择操作的Dialog
        public void showSelectDialog() {
            mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setTitle("选择操作").setItems(new String[]{
                    "修改名称",
                    "删除设备",
                    "重新输入密码"
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            showEditDialog();
                            break;
                        case 1:
                            showDeleteListener(mCurrentPosition);
                            break;
                        case 2:
                            showEnterPassDialog();
                            break;
                    }

                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }


        @Override
        public void bindView(final DeviceEntity entity, final int position) {
            mCurrentEntity = mDevices.get(position);
            mIvHead.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_devices));
            if (entity.isOnLine()) {//在线
                mTvTitle.setText(entity.getTitle());
                mTvTitle.setTextColor(mContext.getResources().getColor(R.color.colorOnLine));
                mTvState.setTextColor(mContext.getResources().getColor(R.color.colorOnLine));
                if (entity.getStren() > -50) {
                    mTvState.setText("当前在线 信号良好");
                } else {
                    mTvState.setText("当前在线 信号欠佳");
                }
                //进入设备详情
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pass = DataLoader.newInstance(mContext).getPass(mCurrentEntity.getSSID(), mCurrentEntity.getPass());
                        mCurrentEntity.setPass(pass);
                        if (pass.isEmpty()) {//密码为空，说明刚才初始化过
                            showEnterPassDialog();
                        } else {
                            Intent intent = new Intent(mContext, DeviceDetailActivity.class);
                            intent.putExtra("Entity", entity);
                            mContext.startActivity(intent);
                        }
                    }
                });
                //设备菜单Dialog
                mBtnChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showSelectDialog();
                    }
                });
            } else { //离线
                mTvTitle.setTextColor(mContext.getResources().getColor(R.color.colorOffLine));
                mTvState.setTextColor(mContext.getResources().getColor(R.color.colorOffLine));
                mTvState.setText("当前离线 请检查设备");
                mBtnChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showNoticeDialog("错误", "此设备已离线，请下拉刷新重试");
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showNoticeDialog("错误", "此设备已离线，请下拉刷新重试");
                    }
                });
            }
        }

        private void showEnterPassDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_enter_pass, null, false);
            builder.setView(layout);
            builder.setTitle("输入设备密码");
            final EditText etPass = (EditText) layout.findViewById(R.id.dialog_pass_et_title);
            final AlertDialog dialog = builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    KLog.e("不添加");
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("连接", null).create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KLog.e("连接");
                    String pass = etPass.getText().toString();
                    KLog.e(pass);
                    if (pass.isEmpty()) {
                        ToastUtils.showToast(mContext, "密码不能为空");
                        return;
                    }
                    if (pass.length() < 8) {
                        ToastUtils.showToast(mContext, "密码长度过短");
                        return;
                    }
                    if (pass.length() > 10) {
                        ToastUtils.showToast(mContext, "密码长度过长");
                        return;
                    }
                    mCurrentEntity.setPass(pass);
                    KLog.e(pass);
                    dialog.dismiss();
                    Intent intent = new Intent(mContext, DeviceDetailActivity.class);
                    intent.putExtra("Entity", mCurrentEntity);
                    mContext.startActivity(intent);

                }
            });
        }
    }
}

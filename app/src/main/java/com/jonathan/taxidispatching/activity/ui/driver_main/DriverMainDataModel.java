package com.jonathan.taxidispatching.activity.ui.driver_main;

import android.util.Log;

import com.jonathan.taxidispatching.APIClient.APIClient;
import com.jonathan.taxidispatching.APIInterface.APIInterface;
import com.jonathan.taxidispatching.Model.StandardResponse;
import com.jonathan.taxidispatching.Model.Taxis;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverMainDataModel {
    public static final String DRIVER_MAIN_DATA_MODEL = "DriverMainDataModel";
    private APIInterface service = APIClient.getClient().create(APIInterface.class);
    public void registerAccount(String platenumber, String password, Integer id, final onDataReadyCallBack callBack) {
        service.registerNewTaxi(platenumber, password, id)
                .enqueue(new Callback<StandardResponse>() {
                    @Override
                    public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                        if(response.body() != null) {
                            if(response.body().success == 1) {
                                callBack.onCallBack("success");
                            } else {
                                callBack.onCallBack(response.body().message);
                            }
                        } else {
                            Log.d(DRIVER_MAIN_DATA_MODEL, "empty body");
                        }
                    }
                    @Override
                    public void onFailure(Call<StandardResponse> call, Throwable t) {
                        callBack.onCallBack("fail to connect to the internet");
                    }
                });
    }

    public void checkDuplicate(String platenumber, final onDataReadyCallBack callBack) {
        service.checkDuplicate(platenumber)
                .enqueue(new Callback<StandardResponse>() {
                    @Override
                    public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                        if(response.body() != null) {
                            if(response.body().success == 1) {
                                callBack.onCallBack("success");
                            } else {
                                callBack.onCallBack(response.body().message);
                            }
                        } else {
                            Log.d(DRIVER_MAIN_DATA_MODEL, "empty body");
                        }
                    }

                    @Override
                    public void onFailure(Call<StandardResponse> call, Throwable t) {
                        callBack.onCallBack("fail to connect to the internet");
                    }
                });
    }

    public void signInTaxi(String platenumber, String password, Integer driverid, final onDataReadyCallBack callBack) {
        service.signInTaxi(platenumber, password, driverid)
                .enqueue(new Callback<StandardResponse>() {
                    @Override
                    public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                        if(response.body() != null) {
                            if(response.body().success == 1) {
                                callBack.onCallBack("success");
                            } else {
                                callBack.onCallBack(response.body().message);
                            }
                        } else {
                            Log.d(DRIVER_MAIN_DATA_MODEL, "empty body");
                        }
                    }

                    @Override
                    public void onFailure(Call<StandardResponse> call, Throwable t) {
                        callBack.onCallBack("fail to connect to the internet");
                    }
                });
    }

    public void checkOwnTaxi(Integer id, final onListCallBack callBack) {
        service.getTaxiList(id)
                .enqueue(new Callback<Taxis>() {
                    @Override
                    public void onResponse(Call<Taxis> call, Response<Taxis> response) {
                        if(response.body() != null) {
                            List<Taxis.Taxi> taxilist = response.body().taxis;
                            String[] taxiNameList = new String[taxilist.size()];
                            if(!taxilist.isEmpty()) {
                                for(int i=0 ;i<taxilist.size(); ++i) {
                                    taxiNameList[i] = taxilist.get(i).platenumber;
                                }
                            } else {
                                taxiNameList[0] = "You have not owned any taxi";
                            }
                            callBack.onCallBack(taxiNameList);
                        } else {
                            Log.d(DRIVER_MAIN_DATA_MODEL, "empty body");
                        }
                    }

                    @Override
                    public void onFailure(Call<Taxis> call, Throwable t) {
                        callBack.onCallBack(null);
                    }
                });
    }

    public void deleteTaxiAccount(String platenumber, String password, final onDataReadyCallBack callBack) {
        service.deleteTaxiAccount(password, platenumber)
                .enqueue(new Callback<StandardResponse>() {
                    @Override
                    public void onResponse(Call<StandardResponse> call, Response<StandardResponse> response) {
                        if(response.body() != null) {
                            if(response.body().success == 1) {
                                callBack.onCallBack("success");
                            } else {
                                callBack.onCallBack(response.body().message);
                            }
                        } else {
                            Log.d(DRIVER_MAIN_DATA_MODEL, "empty body");
                        }
                    }

                    @Override
                    public void onFailure(Call<StandardResponse> call, Throwable t) {
                        callBack.onCallBack("fail to connect to the internet");
                    }
                });
    }

    public interface onDataReadyCallBack {
        public void onCallBack(String message);
    }

    public interface onListCallBack {
        public void onCallBack(String[] data);
    }
}

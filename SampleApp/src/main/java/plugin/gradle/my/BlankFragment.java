package plugin.gradle.my;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import andr.perf.monitor.SamplerFactory;
import andr.perf.monitor.cpu.LooperMonitor;
import andr.perf.monitor.memory.ObjectReferenceSampler;
import andr.perf.monitor.reflect_utils.FieldUtils;
import plugin.gradle.my.dummy.DummyThread;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static List<Activity> ins = new ArrayList<>();

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ins.add(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Button btnBlock;
    private Button btnLeak;
    private Button btnInter;
    private Button btnLoop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank, container, false);
        btnBlock = (Button) v.findViewById(R.id.fragment_block_test_btn);
        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long n = System.nanoTime();
                long m = SystemClock.currentThreadTimeMillis();
                for (int i = 0; i < 20000; i++) {
                    SamplerFactory.getMethodSampler().onMethodEnter("Classssss", "goooo", "int,String");
                    SamplerFactory.getMethodSampler()
                            .onMethodExit("Classssss", "goooo", "int,String");
                    SamplerFactory.getMethodSampler().onMethodExitFinally("Classssss", "goooo", "int,String");
                }
                //                try {
                //                    new DummyThread().start();
                //                    try {
                //                        Thread.sleep(600);
                //                    } catch (InterruptedException e) {
                //                        e.printStackTrace();
                //                    }
                //                } finally {
                //                    Log.i("zkw", "==========thread::>>" + Thread.currentThread().getName());
                //                }

                n = System.nanoTime() - n;
                m = SystemClock.currentThreadTimeMillis() - m;
                n = n / 1000000;
                Log.i("zkw", "-----block test---->nano:" + n + " thread::>>" + m);
            }
        });

        btnLeak = (Button) v.findViewById(R.id.fragment_leak_test_btn);
        btnLeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long n = System.nanoTime();
                long m = SystemClock.currentThreadTimeMillis();
                for (int i = 0; i < 20000; i++) {
                    Object o = new Object();
                    SamplerFactory.getReferenceSampler().onKeyObjectCreate(o);
                    SamplerFactory.getReferenceSampler().onKeyObjectDestroy(o);
                    SamplerFactory.getReferenceSampler().onLowMemory(o);
                }

                n = System.nanoTime() - n;
                m = SystemClock.currentThreadTimeMillis() - m;
                n = n / 1000000;
                Log.i("zkw", "-----leak test---->nano:" + n + " thread::>>" + m);
            }
        });

        btnInter = (Button) v.findViewById(R.id.fragment_interactive_test_btn);
        btnInter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long n = System.nanoTime();
                long m = SystemClock.currentThreadTimeMillis();
                for (int i = 0; i < 20000; i++) {
                    SamplerFactory.getInteractiveSampler().onViewClick(BlankFragment.this, v);
                }

                n = System.nanoTime() - n;
                m = SystemClock.currentThreadTimeMillis() - m;
                n = n / 1000000;
                Log.i("zkw", "-----leak test---->nano:" + n + " thread::>>" + m);
            }
        });
        btnLoop = (Button) v.findViewById(R.id.fragment_loop_test_btn);
        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long n = System.nanoTime();
                long m = SystemClock.currentThreadTimeMillis();
                long ex = 0;
                for (int i = 0; i < 200; i++) {
                    long no = System.nanoTime();
                    for (int j = 0; j < 10; j++) {
                        SamplerFactory.getMethodSampler().onMethodEnter("Classssss", "goooo", "int,String");
                        SamplerFactory.getMethodSampler()
                                .onMethodExit("Classssss", "goooo", "int,String");
                        SamplerFactory.getMethodSampler()
                                .onMethodExitFinally("Classssss", "goooo", "int,String");
                    }
                    ex += System.nanoTime() - no;
                }

                n = System.nanoTime() - n - ex;

                m = SystemClock.currentThreadTimeMillis() - m;
                n = n / 1000000;
                Log.i("zkw", "-----looper monitor test---->nano:" + n + " thread::>>" + m + " exxx:" + ex);
            }
        });
        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

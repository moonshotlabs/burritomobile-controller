package com.audobox.burritomobilecontroller;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends Activity {

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button left = (Button) findViewById(R.id.left);
        Button forward = (Button) findViewById(R.id.forward);
        Button right = (Button) findViewById(R.id.right);
        Button back_left = (Button) findViewById(R.id.back_left);
        Button back = (Button) findViewById(R.id.back);
        Button back_right = (Button) findViewById(R.id.back_right);

        Button restart = (Button) findViewById(R.id.restart);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Burritocontroller", "Control button clicked.");
                switch (v.getId()) {
                    case R.id.left:
                        client.sendCommand('q');
                        break;
                    case R.id.forward:
                        client.sendCommand('w');
                        break;
                    case R.id.right:
                        client.sendCommand('e');
                        break;
                    case R.id.back_left:
                        client.sendCommand('a');
                        break;
                    case R.id.back:
                        client.sendCommand('s');
                        break;
                    case R.id.back_right:
                        client.sendCommand('d');
                        break;
                }
            }
        };

        RepeatListener repeater = new RepeatListener(0, 100, buttonListener);

        left.setOnTouchListener(repeater);
        forward.setOnTouchListener(repeater);
        right.setOnTouchListener(repeater);
        back_left.setOnTouchListener(repeater);
        back.setOnTouchListener(repeater);
        back_right.setOnTouchListener(repeater);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
                MainActivity.this.startActivity(getIntent());

            }
        });

        setupClient();
    }

    public void setupClient() {
        new SocketMaker().execute();
    }

    class Client {

        public OutputStream writer;
        public Socket socket;

        public Client(Socket s, OutputStream w) {
            socket = s;
            writer = w;
        }

        public void sendCommand(final char c) {
            Log.d("Burritocontroller", "trying to send a command: " + c);
            Thread sender = new Thread() {

                @Override
                public void run() {
                    try {
                        Log.d("Burritocontroller", "About to send a command: " + c);
                        writer.write(c);
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            sender.start();
        }
    }

    class SocketMaker extends AsyncTask<Void, Void, Socket> {

        @Override
        protected Socket doInBackground(Void... params) {
            String serverAddress = "ec2-54-215-239-150.us-west-1.compute.amazonaws.com";
//        String serverAddress = "ngrok.com";
            Socket s = null;
            try {
                s = new Socket(serverAddress, 4000);

                return s;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Socket s) {
            OutputStream out = null;
            try {
                out = s.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            client = new Client(s, out);
        }


    }

    @Override
    public void onDestroy() {
        try {
            client.writer.close();
            client.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

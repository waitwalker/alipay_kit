import 'dart:math';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:alipay_kit/alipay_kit.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _alipayKitPlugin = AlipayKitPlatform.instance;

  @override
  void initState() {
    super.initState();
    // initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  // Future<void> initPlatformState() async {
  //   bool isInstalled;
  //   // Platform messages may fail, so we use a try/catch PlatformException.
  //   // We also handle the message potentially returning null.
  //   try {
  //     isInstalled = await _alipayKitPlugin.isInstalled();
  //   } on PlatformException {
  //     isInstalled = false;
  //   }
  //
  //   // If the widget was removed from the tree while the asynchronous platform
  //   // message was in flight, we want to discard the reply rather than calling
  //   // setState to update our non-existent appearance.
  //   if (!mounted) return;
  //
  //   setState(() {
  //     _platformVersion = '$isInstalled';
  //   });
  // }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: KeyDemo2Page(),
      // home: Scaffold(
      //   appBar: AppBar(
      //     title: const Text('Plugin example app'),
      //   ),
      //   body: Center(
      //     child: Text('Running on: $_platformVersion\n'),
      //   ),
      // ),
    );
  }
}

class KeyDemo2Page extends StatefulWidget {
  @override
  _KeyDemo2PageState createState() => _KeyDemo2PageState();
}

class _KeyDemo2PageState extends State<KeyDemo2Page> {
  final List<String> _names = ["1", "2", "3", "4", "5", "6", "7"];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.blue,
        centerTitle: true,
        title: Text("Key demo"),
      ),
      body: ListView(
        children: _names.map((item) {
          return KeyItemLessWidget(item);
        }).toList(),
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.delete),
        onPressed: () {
          setState(() {
            _names.removeAt(0);
          });
        },
      ),
    );
  }
}

class KeyItemLessWidget extends StatefulWidget {
  final String name;

  KeyItemLessWidget(this.name);

  @override
  _KeyItemLessWidgetState createState() => _KeyItemLessWidgetState();
}

class _KeyItemLessWidgetState extends State<KeyItemLessWidget> {
  final randColor = Color.fromARGB(255, Random().nextInt(256), Random().nextInt(256), Random().nextInt(256));

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Text(
        widget.name,
        style: TextStyle(color: Colors.white, fontSize: 50),
      ),
      height: 80,
      color: randColor,
    );
  }
}

// class KeyItemLessWidget extends StatelessWidget {
//   final String name;
//   final randColor = Color.fromARGB(255, Random().nextInt(256), Random().nextInt(256), Random().nextInt(256));
//   KeyItemLessWidget(this.name);
//
//   @override
//   Widget build(BuildContext context) {
//     return Container(
//       child: Text(
//         name,
//         style: TextStyle(color: Colors.white, fontSize: 50),
//       ),
//       height: 80,
//       color: randColor,
//     );
//   }
// }

import 'package:flutter/material.dart';

class Home extends StatelessWidget {
  const Home({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(DateTime.now().toIso8601String())),
      body: Center(
        child: Column(
          children: [
            MaterialButton(
              onPressed: () { debugPrint('忽略电池优化'); },
              child: const Text('忽略电池优化'),
            ),
          ],
        ),
      ),
    );
  }
}

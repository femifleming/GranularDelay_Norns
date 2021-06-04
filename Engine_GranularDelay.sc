Engine_GranularDelay : CroneEngine {

	var buffer, verb, verb_bus, record, granulator;

	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {

	buffer = Buffer.alloc(context.server, 16000 * 8, 1);
	verb_bus = Bus.audio(context.server, 2);

		SynthDef.new(\recorder, {
	arg bufnum, in;
	RecordBuf.ar(Mix.new(In.ar(in)*0.5), bufnum, recLevel: 1);
}).add;

SynthDef.new(\granulator, {
	arg out=0, bufnum;
	var engine = {
		var rate, start, mul;
		rate = LFNoise0.ar(LFNoise0.ar(4, 20, Rand(1, 4)));
		start = LFNoise0.ar(2.rand);
		mul = SinOsc.ar(LFNoise0.ar(Rand(1,8))).round(1/7);
		PlayBuf.ar(1, bufnum, rate, startPos:start, loop:1) * mul
	}!8;
	engine = DelayN.ar(engine, 1, LFNoise0.ar(LFNoise0.ar(4, 2, 2)));
	//engine = MoogFF.ar(engine, LFNoise2.ar(0.2, 3000,3100));
	engine = Splay.ar(engine, SinOsc.ar(Saw.ar(0.2, mul:200, add:200), mul:0.95, add:0.5));
	Out.ar(out, Splay.ar(engine));
}).add;

SynthDef.new(\verb, {
	arg in=0, out=0;
	var sig, local;
	sig = In.ar(in, 2);
	sig = {
		var delTime;
		delTime = LFNoise0.ar(LFNoise0.ar(LFNoise0.ar(LFNoise0.ar(4, 2, 2), 2, 2)));
		DelayN.ar(sig, 1, delTime)
	}!6;
	sig = Splay.ar(sig);
	Out.ar(out, sig);
}).add;

		context.server.sync;

		record = Synth.new(\recorder, [\in, context.in_b[0].index, \bufnum, buffer.bufnum], target: context.xg);
		verb = Synth.new(\verb, [\in, verb_bus, \out, context.out_b.index], target: context.xg);
		granulator = Synth.new(\granulator, [\bufnum, buffer.bufnum, \out, verb_bus], target: context.xg);

	}

	free {
    buffer.free;verb.free;record.free;verb_bus.free;
	}

} 
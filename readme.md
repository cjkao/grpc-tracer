# GRPC  with tracer

# env
jdk 11


#OpenTracing tracer
see [tracer core](https://github.com/jaegertracing/jaeger-client-java/blob/master/jaeger-core/README.md)



# run cmd

- client

```

export JAEGER_REPORTER_LOG_SPANS=true
export JAEGER_SAMPLER_TYPE=const
export JAEGER_SAMPLER_PARAM=1
export JAEGER_SERVICE_NAME=grpc_dict
mvn exec:java -Dexec.mainClass="cj.poc.DictClient"
```

- server

```
export JAEGER_REPORTER_LOG_SPANS=true
export JAEGER_SAMPLER_TYPE=const
export JAEGER_SAMPLER_PARAM=1
export JAEGER_SERVICE_NAME=grpc_server
 mvn exec:java -Dexec.mainClass="cj.poc.DictServer" -Dexec.args="-p main.properties"
```



/*
 * nbg_benchmark.cc
 *
 * This a benchmark application allowing to test Network binary model using
 * OpenVX libraries on VSI VIP accelerator. It provides information about inference
 * times and MAC (Multiply Accumulator) utilization of the input model.
 *
 * Author: Othmane AHL ZOUAOUI <othmane.ahlzouaoui@st.com> for STMicroelectronics.
 *
 * Copyright (c) 2023 STMicroelectronics. All rights reserved.
 *
 * This software component is licensed by ST under BSD 3-Clause license,
 * the "License"; You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *
 *     http://www.opensource.org/licenses/BSD-3-Clause
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sstream>
#include <fstream>
#include <fcntl.h>
#include <errno.h>
#include <math.h>
#include <assert.h>
#include <getopt.h>
#include <iostream>
#include <time.h>
#include <pthread.h>
#include "vnn_utils.h"

#define MAX_DEVICE_CNT 8
#define MAX_INPUT_COUNT  32
#define MAX_OUTPUT_COUNT 32
#define VIP_MAC     (768)
#define GPU_CLK_FD "/sys/kernel/debug/gc/clk"

typedef struct _Args
{
	vx_uint32 deviceIndex;
	vx_int32 loop;
	vx_graph graph;
    uint64_t vipMac;
    uint64_t caseMac;
    uint64_t freq;
}Args;


std::string network_binary_file;
std::string input_file;
uint64_t case_mac = 0;
unsigned int nb_loops = 1;

#define BILLION                                 1000000000
static uint64_t get_perf_count()
{
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (uint64_t)((uint64_t)ts.tv_nsec + (uint64_t)ts.tv_sec * BILLION);
}

/**
 * This function display the help when -h or --help is passed as parameter.
 */
static void print_help(int argc, char** argv)
{
	std::cout <<
		"Usage: " << argv[0] << " -m <nbg_file .nb> -i <input_file .tensor/.txt> -c <int case_mac> -l <int nb_loops>\n"
		"\n"
		"-m --nb_file <.nb file path>:               .nb network binary file to be benchmarked.\n"
		"-i --input_file <.tensor/.txt/ file path>:  Input file to be used for benchmark.\n"
		"-c --case_mac <int>:                        Theorical value of MAC (Multiply Accu) of the model.\n"
		"-l --loops <int>:                           The number of loops of the inference (default loops=1)\n"
		"--help:                                     Show this help\n";
	exit(1);
}

/**
 * This function parse the parameters of the application -m and -l are
 * mandatory.
 */
void process_args(int argc, char** argv)
{
	const char* const short_opts = "m:i:c:l:p:h";
	const option long_opts[] = {
		{"nb_file", required_argument, nullptr, 'm'},
		{"input_file", optional_argument, nullptr, 'i'},
		{"case_mac",   optional_argument, nullptr, 'c'},
		{"loops",      optional_argument, nullptr, 'l'},
		{"help",       no_argument,       nullptr, 'h'},
		{nullptr,      no_argument,       nullptr, 0}
	};

	while (true)
	{
		const auto opt = getopt_long(argc, argv, short_opts, long_opts, nullptr);

		if (-1 == opt)
			break;

		switch (opt)
		{
		case 'm':
			network_binary_file = std::string(optarg);
			std::cout << "Info: Network binary file set to: " << network_binary_file << std::endl;
			break;
		case 'i':
			input_file = std::string(optarg);
			std::cout << "Info: Using " << input_file << " as an input for benchmarking." << std::endl;
			break;
		case 'c':
			case_mac = std::stoi(optarg);
			std::cout << "Info: Using " << case_mac << " as a case MAC for " << network_binary_file << " model." << std::endl;
			break;
		case 'l':
			nb_loops = std::stoi(optarg);
			std::cout << "Info: Executing  " << nb_loops << " inference(s) during this benchmark." << std::endl;
			break;
		case 'h': // -h or --help
		case '?': // Unrecognized option
		default:
			print_help(argc, argv);
			break;
		}
	}
	if (network_binary_file.empty() || argc < 2)
		print_help(argc, argv);
}

/**
 * This functions checks if VIP_MAC and FREQUENCY env variables
 * are set and runs the graph nb_loops times, computes and prints
 * the average inference time and MAC utilization of the model.
*/

vx_status vnn_ProcessGraph(Args *args)
{
    vx_status   status = VX_FAILURE;
    uint64_t tmsStart, tmsEnd;
    float msAvg, usAvg;
    vx_uint32 i = 0;
    float		rUtil = 0, rtime = 0;

    args->vipMac = VIP_MAC;

    std::ifstream gc_clk_fd(GPU_CLK_FD);
    std::string gc_clk_mc;
    if (std::getline(gc_clk_fd, gc_clk_mc)) {
        std::stringstream str_gc_clk_mc(gc_clk_mc);
        std::string gc_clk_mc_freq;
        for (int i = 1; i <= 4; i++) {
            if (str_gc_clk_mc >> gc_clk_mc_freq) {
                if (i == 4) {
                    args->freq = std::stoul(gc_clk_mc_freq);
                    std::cout << "Info: NPU running at frequency: " << args->freq << "Hz." << std::endl;
                }
            }
        }
    }

    status = vxProcessGraph(args->graph);
    _CHECK_STATUS(status, exit);
    std::cout << "Info: Initialized the graph" << std::endl;

	printf("Info: Started running the graph [%d] loops ... device id[%d].\n", args->loop,args->deviceIndex);
    if (args->caseMac == 0){
        std::cout << "Info: No Case MAC has been specified for this model." << std::endl;
        std::cout << "Info: The MAC Utilization cannot be computed." << std::endl;
    }
    tmsStart = get_perf_count();
    for(i = 0; i < args->loop; i++)
    {
        status = vxProcessGraph(args->graph);
        _CHECK_STATUS(status, exit);
    }
    tmsEnd = get_perf_count();
	msAvg = (float)(tmsEnd - tmsStart)/1000000/args->loop;
    usAvg = (float)(tmsEnd - tmsStart)/1000/args->loop;

    if (args->caseMac != 0){
        rtime = (float)(tmsEnd - tmsStart)/1000000000/args->loop;
        rUtil = args->caseMac*1000000 / ((float)((args->vipMac * args->freq)) * rtime);
        printf("Info: MAC utilization is %.2f%% with caseMAC set to %ld Million of MAC\n", rUtil*100, args->caseMac);
    }
    printf("Info: Loop:%d,Average: %.2f ms or %.2f us\n",args->loop, msAvg,usAvg);
exit:
    return status;
}


/*-------------------------------------------
                Main Function
-------------------------------------------*/
int main(int argc, char **argv){
    vx_context  context[MAX_DEVICE_CNT] = {NULL};
    vx_graph    graph[MAX_DEVICE_CNT] = {NULL};
    vx_kernel   kernel[MAX_DEVICE_CNT] = {NULL};
    vx_node     node[MAX_DEVICE_CNT] = {NULL};
    vx_status   status = VX_FAILURE;
    vx_tensor   input_tensor = NULL;

    inout_obj inputs[MAX_DEVICE_CNT][MAX_INPUT_COUNT];
    inout_obj outputs[MAX_DEVICE_CNT][MAX_OUTPUT_COUNT];
	inout_param inputs_param[MAX_DEVICE_CNT][MAX_INPUT_COUNT] ;
	inout_param outputs_param[MAX_DEVICE_CNT][MAX_OUTPUT_COUNT];

    process_args(argc, argv);

	Args args[MAX_DEVICE_CNT] = {NULL};

    vx_int32 input_count  = 0;
    vx_int32 output_count = 0;
	vx_int32 loop = 1;

    uint64_t tmsStart, tmsEnd, msVal, usVal;

    int i=0, ret[MAX_DEVICE_CNT];
    int j=0;

    pthread_t thread[MAX_DEVICE_CNT];

    vx_uint32 deviceCount = 0;
    vx_context ctx = vxCreateContext();
    vxQueryContext(ctx, VX_CONTEXT_DEVICE_COUNT_VIV, &deviceCount, sizeof(deviceCount));
    vxReleaseContext(&ctx);

    loop = vx_int32(nb_loops);

	int num = atoi(argv[argc-2]);
    if(num > 0)
    {
        if(num<deviceCount)
            deviceCount= num;
        printf("change deviceCount as %d\n",deviceCount);
    }

	ZEROS(inputs_param);
	ZEROS(outputs_param);

    for(i = 0;i < deviceCount;i++)
    {
        context[i] = vxCreateContext();
        _CHECK_OBJ(context[i], exit);

        graph[i] = vxCreateGraph(context[i]);
        _CHECK_OBJ(graph[i], exit);

        kernel[i] = vxImportKernelFromURL(context[i], VX_VIVANTE_IMPORT_KERNEL_FROM_FILE, argv[2]);
        status = vxGetStatus((vx_reference)kernel[i]);
        _CHECK_STATUS(status, exit);

        node[i] = vxCreateGenericNode(graph[i], kernel[i]);
        status = vxGetStatus((vx_reference)node[i]);
        _CHECK_STATUS(status, exit);

		status = vnn_QueryInputsAndOutputsParam(kernel[i],inputs_param[i],&input_count,outputs_param[i],&output_count);
		_CHECK_STATUS(status, exit);

        for (j = 0; j < input_count; j++)
        {
            status |= vnn_CreateObject(context[i], &inputs_param[i][j],&inputs[i][j]);
            _CHECK_STATUS(status, exit);
            status |= vxSetParameterByIndex(node[i], j, (vx_reference)inputs[i][j].u.ref);
            _CHECK_STATUS(status, exit);
        }
        for (j = 0; j < output_count; j++)
        {
            status |= vnn_CreateObject(context[i],&outputs_param[i][j],&outputs[i][j]);
            _CHECK_STATUS(status, exit);
            status |= vxSetParameterByIndex(node[i], j+input_count, (vx_reference)outputs[i][j].u.ref);;
            _CHECK_STATUS(status, exit);
        }
		args[i].deviceIndex = i;
		args[i].loop = loop;
		args[i].graph = graph[i];
        args[i].caseMac = case_mac;

        std::cout << "Info: Set device : " << args[i].deviceIndex  << std::endl;
        vxSetGraphAttribute(graph[i], VX_GRAPH_DEVICE_INDEX_VIV, &args[i].deviceIndex, sizeof(args[i].deviceIndex)) ;

        std::cout << "Info: Compiling and verifying graph..." << std::endl;
        tmsStart = get_perf_count();
		status = vxVerifyGraph(graph[i]);
        _CHECK_STATUS(status, exit);
        tmsEnd = get_perf_count();
        msVal = (tmsEnd - tmsStart)/1000000;
        usVal = (tmsEnd - tmsStart)/1000;
        printf("Info: Verifying graph took: %ldms or %ldus\n", msVal, usVal);

        if (input_file.empty()){
            inout_obj* obj = &inputs[i][0];
            input_tensor = obj->u.tensor;
            vnn_LoadTensorRandom(input_tensor);
            _CHECK_STATUS(status, exit);
        } else {
            for (j = 0; j < input_count; j++){
			    status = vnn_LoadDataFromFile(&inputs[i][0],argv[4+j]);
			    _CHECK_STATUS(status, exit);
            }
        }
    }
    for(i=0;i<deviceCount;i++)
    {
        ret[i] = pthread_create(&thread[i], NULL, (void *(*)(void *))vnn_ProcessGraph, &args[i] );
    }

    for(i=0;i<deviceCount;i++)
        pthread_join(thread[i], NULL);

	for(i = 0;i < deviceCount;i++)
    {
		for( j = 0;j < output_count;j++)
		{
			char filename[128];
			sprintf(filename,"output%d_%d.txt",i,j);
			status = vnn_SaveDataToFile(&outputs[i][j],filename);
			_CHECK_STATUS(status, exit);
		}
    }

exit:
    for(i = 0;i < deviceCount;i++)
    {
         for (j = 0; j < input_count; j++)
         {
            status = vnn_ReleaseObject(&inputs[i][j]);
         }
         for (j=0; j < output_count; j++)
         {
            status = vnn_ReleaseObject(&outputs[i][j]);
         }
        if (kernel[i] != NULL)
            vxReleaseKernel(&kernel[i]);
        if (node[i] != NULL)
            vxReleaseNode(&node[i]);
        if (graph[i] != NULL)
            vxReleaseGraph(&graph[i]);
        if (context[i] != NULL)
            vxReleaseContext(&context[i]);
    }
    return 0;
}

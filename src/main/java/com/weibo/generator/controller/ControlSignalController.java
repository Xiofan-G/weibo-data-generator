package com.weibo.generator.controller;

import com.alibaba.fastjson.JSONObject;
import com.weibo.generator.entity.CitiBikeEdgeLabel;
import com.weibo.generator.entity.CitiBikeVertexLabel;
import com.weibo.generator.entity.RelationLabel;
import com.weibo.generator.entity.VertexLabel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class ControlSignalController {

    @RequestMapping("weibo")
    public String weibo() {
        return "weibo";
    }

    @RequestMapping("labels")
    @ResponseBody
    public JSONObject getLabels(@RequestParam(name = "type", required = false) String labelType) {
        JSONObject result = new JSONObject();
        if (labelType == null || "".equals(labelType)) {

            ArrayList<String> vertexLabels = new ArrayList<>();
            for (VertexLabel label : VertexLabel.values()) {
                vertexLabels.add(label.name());
            }

            result.put("vertexLabels", vertexLabels);

            ArrayList<String> edgeLabels = new ArrayList<>();
            for (RelationLabel label : RelationLabel.values()) {
                if (label.getLabel().equals(RelationLabel.Control.getLabel()))
                    continue;
                edgeLabels.add(label.name());
            }
            result.put("edgeLabels", edgeLabels);

        } else {

            ArrayList<String> vertexLabels = new ArrayList<>();
            for (CitiBikeVertexLabel label : CitiBikeVertexLabel.values()) {
                vertexLabels.add(label.name());
            }

            result.put("vertexLabels", vertexLabels);

            ArrayList<String> edgeLabels = new ArrayList<>();
            for (CitiBikeEdgeLabel label : CitiBikeEdgeLabel.values()) {
                if (label.getLabel().equals(CitiBikeEdgeLabel.Control.getLabel()))
                    continue;
                edgeLabels.add(label.name());
            }
            result.put("edgeLabels", edgeLabels);
        }


        return result;
    }

}

package com.weibo.generator.controller;

import com.alibaba.fastjson.JSONObject;
import com.weibo.generator.entity.RelationLabel;
import com.weibo.generator.entity.VertexLabel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public JSONObject getLabels() {
        JSONObject result = new JSONObject();
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
        return result;
    }

}

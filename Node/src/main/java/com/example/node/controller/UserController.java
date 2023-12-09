package com.example.node.controller;

import com.example.node.Queries.create.CreateDocumentQuery;
import com.example.node.model.query.OperationType;
import com.example.node.model.request.JwtRequest;
import com.example.node.model.request.QueryRequest;
import com.example.node.model.response.JwtResponse;
import com.example.node.model.response.QueryResponse;
import com.example.node.model.system.Affinity;
import com.example.node.model.system.Node;
import com.example.node.model.system.User;
import com.example.node.services.CustomUserDetailService;
import com.example.node.util.JSONUtil;
import com.example.node.util.system.JWTUtil;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final JWTUtil jwtUtility;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;

    public UserController(JWTUtil jwtUtility, AuthenticationManager authenticationManager, CustomUserDetailService customUserDetailService) {
        this.jwtUtility = jwtUtility;
        this.authenticationManager = authenticationManager;
        this.customUserDetailService = customUserDetailService;
    }

    @PostMapping("/authenticate")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {
        System.out.println("token");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        System.out.println("user");
        final UserDetails userDetails
                = customUserDetailService.loadUserByUsername(jwtRequest.getUsername());

        final String token =
                jwtUtility.generateToken(userDetails);
        System.out.println(token);
        return new JwtResponse(token);
    }

    @PostMapping("/register")
    public QueryResponse register(@RequestBody QueryRequest queryRequest) {
        User user = JSONUtil.parseObject(new JSONObject(queryRequest.getBody()), User.class);
        String nodeId = user.get_id();
        QueryResponse userResponse = new CreateDocumentQuery().performQuery(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_DOCUMENT)
                        .database("System")
                        .collection("User")
                        .body(new JSONObject(user).toMap())
                        .build()
        );
        User dbUser = JSONUtil.parseObject(new JSONObject(userResponse.getJsonObject()), User.class);
        Affinity userAffinity = Affinity.builder()
                ._nodeId(nodeId)
                ._documentId(dbUser.get_id())
                .build();

        new CreateDocumentQuery().performQuery(
                QueryRequest.builder()
                        .operation(OperationType.CREATE_DOCUMENT)
                        .database("System")
                        .collection("Affinity")
                        .body(new JSONObject(userAffinity).toMap())
                        .build()
        );

        return userResponse;
    }
}

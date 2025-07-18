newman
sprint13/develop
❏ users
↳ User create
POST http://localhost:8080/users  
201 Created ★ 188ms time ★ 386B↑ 260B↓ size ★ 9↑ 5↓ headers ★ 0 cookies
┌ ↑ raw ★ 101B
│ {
│   "login": "dolore",
│   "name": "Nick Name",
│   "email": "mail@mail.ru",
│   "birthday": "1946-08-20"
│ }
└
┌ ↓ application/json ★ text ★ json ★ utf8 ★ 91B
│ {"id":1,"email":"mail@mail.ru","login":"dolore","name"
│ :"Nick Name","birthday":"1946-08-20"}
└
prepare wait dns-lookup tcp-handshake transfer-start download process total
6ms 4ms 790µs 633µs 176ms 5ms 336µs 194ms
✓ Status code is 200 or 201
✓ Has user create response
✓ Test user 'id' field
✓ Test user 'email' field
✓ Test user 'name' field
✓ Test user 'login' field
✓ Test user 'birthday' field
↳ User create Fail login
POST http://localhost:8080/users  
400 Bad Request ★ 20ms time ★ 372B↑ 244B↓ size ★ 9↑ 4↓ headers ★ 0 cookies
┌ ↑ raw ★ 88B
│ {
│   "login": "dolore ullamco",
│   "email": "yandex@mail.ru",
│   "birthday": "2446-08-20"
│ }
└
┌ ↓ application/json ★ text ★ json ★ utf8 ★ 100B
│ {"errors":{"birthday":"Birthday cannot be in the futur
│ e","login":"Login cannot contain whitespace"}}
└
prepare wait dns-lookup tcp-handshake transfer-start download process total
669µs 495µs   (cache)      (cache)         17ms 1ms 70µs 20ms  
at assertion:7 in
test-script                                                                                                  
inside "Genre / Film id=1 update remove genre"

146. AssertionError Test film 'genres'
     field                                                                                                       
     expected { Object (errors) } to have property '
     genres'                                                                         
     at assertion:8 in
     test-script                                                                                                  
     inside "Genre / Film id=1 update remove genre"

147. AssertionError Status code is
     200                                                                                                             
     expected response to have status reason 'OK' but got 'INTERNAL SERVER
     ERROR'                                                   
     at assertion:0 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

148. AssertionError Test film 'id'
     field                                                                                                           
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

149. AssertionError Test film 'name'
     field                                                                                                         
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

150. AssertionError Test film 'description'
     field                                                                                                  
     expected { Object (error) } to have property '
     description'                                                                     
     at assertion:4 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

151. AssertionError Test film 'releaseDate'
     field                                                                                                  
     expected { Object (error) } to have property '
     releaseDate'                                                                     
     at assertion:5 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

152. AssertionError Test film 'duration'
     field                                                                                                     
     expected { Object (error) } to have property '
     duration'                                                                        
     at assertion:6 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

153. AssertionError Test film 'mpa'
     field                                                                                                          
     expected { Object (error) } to have property '
     mpa'                                                                             
     at assertion:7 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

154. AssertionError Test film 'genres'
     field                                                                                                       
     expected { Object (error) } to have property '
     genres'                                                                          
     at assertion:8 in
     test-script                                                                                                  
     inside "Genre / Film id=1 get without genre"

155. AssertionError Status code is 200 or
     201                                                                                                      
     expected 500 to be one
     of [ 200, 201 ]                                                                                         
     at assertion:0 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

156. AssertionError Test film 'id'
     field                                                                                                           
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

157. AssertionError Test film 'name'
     field                                                                                                         
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

158. AssertionError Test film 'description'
     field                                                                                                  
     expected { Object (error) } to have property '
     description'                                                                     
     at assertion:4 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

159. AssertionError Test film 'releaseDate'
     field                                                                                                  
     expected { Object (error) } to have property '
     releaseDate'                                                                     
     at assertion:5 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

160. AssertionError Test film 'duration'
     field                                                                                                     
     expected { Object (error) } to have property '
     duration'                                                                        
     at assertion:6 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

161. AssertionError Test film 'mpa.id'
     field                                                                                                       
     expected { Object (error) } to have property '
     mpa'                                                                             
     at assertion:7 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

162. AssertionError Test film 'genres'
     field                                                                                                       
     expected { Object (error) } to have property '
     genres'                                                                          
     at assertion:8 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update"

163. AssertionError Status code is
     200                                                                                                             
     expected response to have status reason 'OK' but got 'INTERNAL SERVER
     ERROR'                                                   
     at assertion:0 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

164. AssertionError Test film 'id'
     field                                                                                                           
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

165. AssertionError Test film 'name'
     field                                                                                                         
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

166. AssertionError Test film 'description'
     field                                                                                                  
     expected { Object (error) } to have property '
     description'                                                                     
     at assertion:4 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

167. AssertionError Test film 'releaseDate'
     field                                                                                                  
     expected { Object (error) } to have property '
     releaseDate'                                                                     
     at assertion:5 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

168. AssertionError Test film 'duration'
     field                                                                                                     
     expected { Object (error) } to have property '
     duration'                                                                        
     at assertion:6 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

169. AssertionError Test film 'mpa.id'
     field                                                                                                       
     expected { Object (error) } to have property '
     mpa'                                                                             
     at assertion:7 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

170. AssertionError Test film 'genres'
     field                                                                                                       
     expected { Object (error) } to have property '
     genres'                                                                          
     at assertion:8 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genres"

171. AssertionError Status code is 200 or
     201                                                                                                      
     expected 500 to be one
     of [ 200, 201 ]                                                                                         
     at assertion:0 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

172. AssertionError Test film 'id'
     field                                                                                                           
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

173. AssertionError Test film 'name'
     field                                                                                                         
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

174. AssertionError Test film 'description'
     field                                                                                                  
     expected { Object (error) } to have property '
     description'                                                                     
     at assertion:4 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

175. AssertionError Test film 'releaseDate'
     field                                                                                                  
     expected { Object (error) } to have property '
     releaseDate'                                                                     
     at assertion:5 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

176. AssertionError Test film 'duration'
     field                                                                                                     
     expected { Object (error) } to have property '
     duration'                                                                        
     at assertion:6 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

177. AssertionError Test film 'mpa.id'
     field                                                                                                       
     expected { Object (error) } to have property '
     mpa'                                                                             
     at assertion:7 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

178. AssertionError Test film 'genres'
     field                                                                                                       
     expected { Object (error) } to have property '
     genres'                                                                          
     at assertion:8 in
     test-script                                                                                                  
     inside "Genre / Film id=2 genres update with duplicate"

179. AssertionError Status code is
     200                                                                                                             
     expected response to have status reason 'OK' but got 'INTERNAL SERVER
     ERROR'                                                   
     at assertion:0 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

180. AssertionError Test film 'id'
     field                                                                                                           
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

181. AssertionError Test film 'name'
     field                                                                                                         
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

182. AssertionError Test film 'description'
     field                                                                                                  
     expected { Object (error) } to have property '
     description'                                                                     
     at assertion:4 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

183. AssertionError Test film 'releaseDate'
     field                                                                                                  
     expected { Object (error) } to have property '
     releaseDate'                                                                     
     at assertion:5 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

184. AssertionError Test film 'duration'
     field                                                                                                     
     expected { Object (error) } to have property '
     duration'                                                                        
     at assertion:6 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

185. AssertionError Test film 'mpa.id'
     field                                                                                                       
     expected { Object (error) } to have property '
     mpa'                                                                             
     at assertion:7 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

186. AssertionError Test film 'genres'
     field                                                                                                       
     expected { Object (error) } to have property '
     genres'                                                                          
     at assertion:8 in
     test-script                                                                                                  
     inside "Genre / Film id=2 get with genre without duplicate"

187. AssertionError Status code is
     200                                                                                                             
     expected response to have status reason 'OK' but got 'INTERNAL SERVER
     ERROR'                                                   
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Get all directors before create"

188. AssertionError Test list directors
     response                                                                                                   
     List length must be 0: expected undefined to deeply equal
     +0                                                                   
     at assertion:1 in
     test-script                                                                                                  
     inside "add-director / Get all directors before create"

189. AssertionError Status code is
     404                                                                                                             
     expected response to have status code 404 but got
     500                                                                          
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Get director 1 before create"

190. AssertionError Status code is 200 or
     201                                                                                                      
     expected 500 to be one
     of [ 200, 201 ]                                                                                         
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Create Director id=1"

191. AssertionError Test director 'id'
     field                                                                                                       
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "add-director / Create Director id=1"

192. AssertionError Test director 'name'
     field                                                                                                     
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "add-director / Create Director id=1"

193. AssertionError Status code is
     200                                                                                                             
     expected response to have status reason 'OK' but got 'INTERNAL SERVER
     ERROR'                                                   
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Get director id=1 after create"

194. AssertionError Test director 'id'
     field                                                                                                       
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "add-director / Get director id=1 after create"

195. AssertionError Test director 'name'
     field                                                                                                     
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "add-director / Get director id=1 after create"

196. AssertionError Status code is
     200                                                                                                             
     expected response to have status code 200 but got
     500                                                                          
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Update Director id=1"

197. AssertionError Test director 'id'
     field                                                                                                       
     expected { Object (error) } to have property '
     id'                                                                              
     at assertion:2 in
     test-script                                                                                                  
     inside "add-director / Update Director id=1"

198. AssertionError Test director 'name'
     field                                                                                                     
     expected { Object (error) } to have property '
     name'                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "add-director / Update Director id=1"

199. AssertionError Status code is
     404                                                                                                             
     expected response to have status code 404 but got
     500                                                                          
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Update unknown director id=10"

200. AssertionError Status code is
     404                                                                                                             
     expected response to have status code 404 but got
     500                                                                          
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Get unknown director 10"

201. AssertionError Status code is
     200                                                                                                             
     expected response to have status reason 'OK' but got 'INTERNAL SERVER
     ERROR'                                                   
     at assertion:0 in
     test-script                                                                                                  
     inside "add-director / Get all directors"

202. AssertionError Test list directors
     response                                                                                                   
     List length must be 1: expected undefined to deeply equal
     1                                                                    
     at assertion:1 in
     test-script                                                                                                  
     inside "add-director / Get all directors"

203. AssertionError Test director[0] 'id'
     field                                                                                                    
     Target cannot be null or
     undefined.                                                                                            
     at assertion:2 in
     test-script                                                                                                  
     inside "add-director / Get all directors"

204. AssertionError Test director[0] 'name'
     field                                                                                                  
     Target cannot be null or
     undefined.                                                                                            
     at assertion:3 in
     test-script                                                                                                  
     inside "add-director / Get all directors"

205. AssertionError Status code is
     200                                                                                                             
     expected response to have status reason 'OK' but got 'BAD REQUEST'  